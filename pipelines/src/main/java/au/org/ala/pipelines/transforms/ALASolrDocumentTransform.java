package au.org.ala.pipelines.transforms;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.gbif.pipelines.core.converters.MultimediaConverter;
import org.gbif.pipelines.core.utils.TemporalUtils;
import org.gbif.pipelines.io.avro.*;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.avro.Schema.Type.UNION;
import static org.gbif.pipelines.common.PipelinesVariables.Metrics.AVRO_TO_JSON_COUNT;

@AllArgsConstructor(staticName = "create")
public class ALASolrDocumentTransform implements Serializable {

    private static final long serialVersionUID = 1279313931024806169L;
    // Core
    @NonNull
    private final TupleTag<ExtendedRecord> erTag;
    @NonNull
    private final TupleTag<BasicRecord> brTag;
    @NonNull
    private final TupleTag<TemporalRecord> trTag;
    @NonNull
    private final TupleTag<LocationRecord> lrTag;

    private final TupleTag<TaxonRecord> txrTag;
    @NonNull
    private final TupleTag<ALATaxonRecord> atxrTag;
    // Extension
    @NonNull
    private final TupleTag<MultimediaRecord> mrTag;
    @NonNull
    private final TupleTag<ImageRecord> irTag;
    @NonNull
    private final TupleTag<AudubonRecord> arTag;
    @NonNull
    private final TupleTag<MeasurementOrFactRecord> mfrTag;

    private final TupleTag<AustraliaSpatialRecord> asrTag;

    @NonNull
    private final PCollectionView<MetadataRecord> metadataView;

    public ParDo.SingleOutput<KV<String, CoGbkResult>, SolrInputDocument> converter() {

        DoFn<KV<String, CoGbkResult>, SolrInputDocument> fn = new DoFn<KV<String, CoGbkResult>, SolrInputDocument>() {

            private final Counter counter = Metrics.counter(ALASolrDocumentTransform.class, AVRO_TO_JSON_COUNT);

            @ProcessElement
            public void processElement(ProcessContext c) {
                CoGbkResult v = c.element().getValue();
                String k = c.element().getKey();

                // Core
                MetadataRecord mdr = c.sideInput(metadataView);
                ExtendedRecord er = v.getOnly(erTag, ExtendedRecord.newBuilder().setId(k).build());
                BasicRecord br = v.getOnly(brTag, BasicRecord.newBuilder().setId(k).build());
                TemporalRecord tr = v.getOnly(trTag, TemporalRecord.newBuilder().setId(k).build());
                LocationRecord lr = v.getOnly(lrTag, LocationRecord.newBuilder().setId(k).build());
                TaxonRecord txr = null;
                if(txrTag != null) {
                    txr = v.getOnly(txrTag, TaxonRecord.newBuilder().setId(k).build());
                }

                // Extension
                MultimediaRecord mr = v.getOnly(mrTag, MultimediaRecord.newBuilder().setId(k).build());
                ImageRecord ir = v.getOnly(irTag, ImageRecord.newBuilder().setId(k).build());
                AudubonRecord ar = v.getOnly(arTag, AudubonRecord.newBuilder().setId(k).build());
                MeasurementOrFactRecord mfr = v.getOnly(mfrTag, MeasurementOrFactRecord.newBuilder().setId(k).build());

                // ALA specific
                ALATaxonRecord atxr = v.getOnly(atxrTag, ALATaxonRecord.newBuilder().setId(k).build());

                AustraliaSpatialRecord asr = null;
                if (asrTag != null){
                    asr = v.getOnly(asrTag, AustraliaSpatialRecord.newBuilder().setId(k).build());
                }


                MultimediaRecord mmr = MultimediaConverter.merge(mr, ir, ar);

                Set<String> skipKeys = new HashSet<String>();
                skipKeys.add("id");
                skipKeys.add("created");
                skipKeys.add("text");
                skipKeys.add("name");
                skipKeys.add("coreRowType");
                skipKeys.add("coreTerms");
                skipKeys.add("extensions");
                skipKeys.add("usage");
                skipKeys.add("classification");
                skipKeys.add("issues");
                skipKeys.add("eventDate");
                skipKeys.add("hasCoordinate");
                skipKeys.add("hasGeospatialIssue");
                skipKeys.add("gbifId");
                skipKeys.add("crawlId");
                skipKeys.add("networkKeys");
                skipKeys.add("protocol");

                SolrInputDocument doc = new SolrInputDocument();
                doc.setField("id", er.getId());

                addToDoc(lr, doc, skipKeys);
                addToDoc(tr, doc, skipKeys);
                addToDoc(br, doc, skipKeys);
                addToDoc(er, doc, skipKeys);
                addToDoc(mdr, doc, skipKeys);
                addToDoc(mdr, doc, skipKeys);

                //add event date
                try {
                    if (tr.getEventDate() != null && tr.getEventDate().getGte() != null) {
                        doc.setField("eventDateSingle", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(tr.getEventDate().getGte()));
                    } else {
                        TemporalUtils.getTemporal(tr.getYear(), tr.getMonth(), tr.getDay())
                                .ifPresent(x -> doc.setField("eventDateSingle", x));
                    }
                } catch (ParseException e){
                    //ignore for now
                }

                if (txr != null) {
                    //add the classification
                    List<RankedName> taxonomy = txr.getClassification();
                    for (RankedName entry : taxonomy) {
                        doc.setField("gbif_s_" + entry.getRank().toString().toLowerCase() + "_id", entry.getKey());
                        doc.setField("gbif_s_" + entry.getRank().toString().toLowerCase(), entry.getName());
                    }

                    String rank = txr.getAcceptedUsage().getRank().toString();
                    doc.setField("gbif_s_rank", txr.getAcceptedUsage().getRank().toString());
                    doc.setField("gbif_s_scientificName", txr.getAcceptedUsage().getName().toString());
                }

                Map<String,String> raw = er.getCoreTerms();
                for (Map.Entry<String, String> entry : raw.entrySet()) {
                    String key = entry.getKey();
                    if(key.startsWith("http")){
                        key = key.substring(key.lastIndexOf("/") + 1);
                    }
                    doc.setField("raw_" + key, entry.getValue().toString());
                }

                if(lr.getDecimalLatitude() != null && lr.getDecimalLongitude() != null){
                    addGeo(doc, lr.getDecimalLatitude(), lr.getDecimalLongitude());
                }

                //TODO assertions
                if (atxr.getTaxonConceptID() != null){
                    List<Schema.Field> fields = atxr.getSchema().getFields();
                    for (Schema.Field field: fields){
                        field.name();
                        Object value = atxr.get(field.name());
                        if (value != null && !field.name().equals("speciesGroup")&& !field.name().equals("speciesSubgroup")){
                            if (value instanceof Integer){
                                doc.setField(field.name(), value);
                            } else {
                                doc.setField(field.name(), value.toString());
                            }
                        }
                    }

                    //required for EYA
                    doc.setField( "names_and_lsid", atxr.getScientificName() + "|" + atxr.getTaxonConceptID() + "|" + atxr.getVernacularName() + "|" + atxr.getKingdom() + "|" + atxr.getFamily()); // is set to IGNORE in headerAttributes
                    doc.setField( "common_name_and_lsid",  atxr.getVernacularName() + "|" + atxr.getScientificName() + "|" + atxr.getTaxonConceptID() + "|" +  atxr.getVernacularName() + "|" + atxr.getKingdom() + "|" + atxr.getFamily()); // is set to IGNORE in headerAttributes

                    //legacy fields referenced in biocache-service code
                    doc.setField("taxon_name", atxr.getScientificName());
                    doc.setField("lsid", atxr.getTaxonConceptID());
                    doc.setField("rank", atxr.getRank());
                    doc.setField("rank_id", atxr.getRankID());

                    for (String s : atxr.getSpeciesGroup()){
                        doc.setField("species_group", s);
                    }
                    for (String s : atxr.getSpeciesSubgroup()){
                        doc.setField("species_subgroup", s);
                    }
                }

                doc.setField("geospatial_kosher", lr.getHasCoordinate());
                doc.setField("first_loaded_date", new Date());

                //add sampling
                if (asr != null) {
                    Map<String, String> samples = asr.getItems();
                    for (Map.Entry<String, String> sample : samples.entrySet()) {
                        if (!StringUtils.isEmpty(sample.getValue())) {
                            if (sample.getKey().startsWith("el")) {
                                doc.setField(sample.getKey(), Double.valueOf(sample.getKey()));
                            } else {
                                doc.setField(sample.getKey(), sample.getValue());
                            }
                        }
                    }
                }

                //legacy fields reference directly in biocache-service code
                if (txr != null) {
                    IssueRecord taxonomicIssues = txr.getIssues();
                    for(String issue : taxonomicIssues.getIssueList()){
                        doc.setField("assertions", issue);
                    }
                }

                IssueRecord geospatialIssues = lr.getIssues();
                for(String issue : geospatialIssues.getIssueList()){
                    doc.setField("assertions", issue);
                }

                IssueRecord temporalIssues = tr.getIssues();
                for(String issue : temporalIssues.getIssueList()){
                    doc.setField("assertions", issue);
                }

                IssueRecord basisOfRecordIssues = br.getIssues();
                for(String issue : basisOfRecordIssues.getIssueList()){
                    doc.setField("assertions", issue);
                }

                for(String issue : mdr.getIssues().getIssueList()){
                    doc.setField("assertions", issue);
                }

                c.output(doc);

                counter.inc();
            }
        };

        return ParDo.of(fn).withSideInputs(metadataView);
    }


    void addGeo(SolrInputDocument doc, double lat, double lon){
        String latlon = "";
        //ensure that the lat longs are in the required range before
        if (lat <= 90 && lat >= -90d && lon <= 180 && lon >= -180d) {
            //https://lucene.apache.org/solr/guide/7_0/spatial-search.html#indexing-points
            latlon = lat + "," + lon; //required format for indexing geodetic points in SOLR
        }

        doc.addField("lat_long", latlon); // is set to IGNORE in headerAttributes
        doc.addField("point-1", getLatLongString(lat, lon, "#")); // is set to IGNORE in headerAttributes
        doc.addField("point-0.1", getLatLongString(lat, lon, "#.#")); // is set to IGNORE in headerAttributes
        doc.addField("point-0.01", getLatLongString(lat, lon, "#.##")); // is set to IGNORE in headerAttributes
        doc.addField("point-0.02", getLatLongStringStep(lat, lon, "#.##", 0.02)); // is set to IGNORE in headerAttributes
        doc.addField("point-0.001", getLatLongString(lat, lon, "#.###")); // is set to IGNORE in headerAttributes
        doc.addField("point-0.0001", getLatLongString(lat, lon, "#.####")); // is set to IGNORE in headerAttributes
    }

    String getLatLongStringStep(Double lat, Double lon, String format, Double step) {
        DecimalFormat df = new DecimalFormat(format);
        //By some "strange" decision the default rounding model is HALF_EVEN
        df.setRoundingMode(java.math.RoundingMode.HALF_UP);
        return df.format(Math.round(lat / step) * step) + "," + df.format(Math.round(lon / step) * step);
    }

    /**
     * Returns a lat,long string expression formatted to the supplied Double format
     */
    String getLatLongString(Double lat, Double lon, String format) {
        DecimalFormat df = new DecimalFormat(format);
        //By some "strange" decision the default rounding model is HALF_EVEN
        df.setRoundingMode(java.math.RoundingMode.HALF_UP);
        return df.format(lat) + "," + df.format(lon);
    }

    void addToDoc(SpecificRecordBase record, SolrInputDocument doc, Set<String> skipKeys){

        record.getSchema().getFields().stream()
                .filter(n -> !skipKeys.contains(n.name()))
                .forEach(
                        f -> Optional.ofNullable(record.get(f.pos())).ifPresent(r -> {

                            Schema schema = f.schema();
                            Optional<Schema.Type> type = schema.getType() == UNION
                                    ? schema.getTypes().stream().filter(t -> t.getType() != Schema.Type.NULL).findFirst().map(Schema::getType)
                                    : Optional.of(schema.getType());

                            type.ifPresent(t -> {
                                switch (t) {
                                    case BOOLEAN:
                                        doc.setField(f.name(), (Boolean) r);
                                        break;
                                    case FLOAT:
                                        doc.setField(f.name(), (Float) r);
                                        break;
                                    case DOUBLE:
                                        doc.setField(f.name(), (Double) r);
                                        break;
                                    case INT:
                                        doc.setField(f.name(), (Integer) r);
                                        break;
                                    case LONG:
                                        doc.setField(f.name(), (Long) r);
                                        break;
                                    default:
                                        doc.setField(f.name(), r.toString());
                                        break;
                                }
                            });
                        })
                );
    }
}
