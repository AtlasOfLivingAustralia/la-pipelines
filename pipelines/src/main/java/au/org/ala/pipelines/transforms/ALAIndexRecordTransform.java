package au.org.ala.pipelines.transforms;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

/**
 * A SOLR transform that aims to provide a index that is backwards compatible with
 * ALA's biocache-service.
 */
@Slf4j
public class ALAIndexRecordTransform implements Serializable {

    private static final long serialVersionUID = 1279313931024806169L;
    // Core
    @NonNull
    private TupleTag<ExtendedRecord> erTag;
    @NonNull
    private TupleTag<BasicRecord> brTag;
    @NonNull
    private TupleTag<TemporalRecord> trTag;
    @NonNull
    private TupleTag<LocationRecord> lrTag;

    private TupleTag<TaxonRecord> txrTag;
    @NonNull
    private TupleTag<ALATaxonRecord> atxrTag;
    // Extension
    @NonNull
    private TupleTag<MultimediaRecord> mrTag;
    @NonNull
    private TupleTag<ImageRecord> irTag;
    @NonNull
    private TupleTag<AudubonRecord> arTag;
    @NonNull
    private TupleTag<MeasurementOrFactRecord> mfrTag;

    private TupleTag<AustraliaSpatialRecord> asrTag;

    private TupleTag<ALAAttributionRecord> aarTag;

    @NonNull
    private  PCollectionView<MetadataRecord> metadataView;

    String datasetID;

    public static ALAIndexRecordTransform create(
            TupleTag<ExtendedRecord> erTag,
            TupleTag<BasicRecord> brTag,
            TupleTag<TemporalRecord> trTag,
            TupleTag<LocationRecord> lrTag,
            TupleTag<TaxonRecord> txrTag,
            TupleTag<ALATaxonRecord> atxrTag,
            TupleTag<MultimediaRecord> mrTag,
            TupleTag<ImageRecord> irTag,
            TupleTag<AudubonRecord> arTag,
            TupleTag<MeasurementOrFactRecord> mfrTag,
            TupleTag<AustraliaSpatialRecord> asrTag,
            TupleTag<ALAAttributionRecord> aarTag,
            PCollectionView<MetadataRecord> metadataView,
            String datasetID
    ){
        ALAIndexRecordTransform t = new ALAIndexRecordTransform();
        t.erTag = erTag;
        t.brTag = brTag;
        t.trTag = trTag;
        t.lrTag = lrTag;
        t.txrTag = txrTag;
        t.atxrTag = atxrTag;
        t.mrTag = mrTag;
        t.irTag = irTag;
        t.arTag = arTag;
        t.mfrTag = mfrTag;
        t.asrTag =  asrTag;
        t.aarTag  = aarTag;
        t.metadataView = metadataView;
        t.datasetID = datasetID;
        return t;
    }

    public ParDo.SingleOutput<KV<String, CoGbkResult>, ALAIndexRecord> converter() {

        DoFn<KV<String, CoGbkResult>, ALAIndexRecord> fn = new DoFn<KV<String, CoGbkResult>, ALAIndexRecord>() {

            private final Counter counter = Metrics.counter(ALAIndexRecordTransform.class, AVRO_TO_JSON_COUNT);

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
                ALAAttributionRecord aar = v.getOnly(aarTag, ALAAttributionRecord.newBuilder().setId(k).build());

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
                skipKeys.add("issues");
                skipKeys.add("machineTags"); //TODO review content

                ALAIndexRecord doc = new ALAIndexRecord();
                doc.setStringProperties(new HashMap<String, String>());
                doc.setIntProperties(new HashMap<String, Integer>());
                doc.setLongProperties(new HashMap<String, Long>());
                doc.setBooleanProperties(new HashMap<String, Boolean>());
                doc.setDoubleProperties(new HashMap<String, Double>());
                doc.setFloatProperties(new HashMap<String, Float>());
                doc.setMultiValueProperties(new HashMap<String, List<String>>());



                setField(doc, "id", er.getId());

                addToDoc(lr, doc, skipKeys);
                addToDoc(tr, doc, skipKeys);
                addToDoc(br, doc, skipKeys);
                addToDoc(er, doc, skipKeys);
                addToDoc(mdr, doc, skipKeys);
                addToDoc(mdr, doc, skipKeys);

                //add event date
//                try {
//                    if (tr.getEventDate() != null && tr.getEventDate().getGte() != null) {
//                        setField(doc,"eventDateSingle", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(tr.getEventDate().getGte()));
//                    } else {
//                        TemporalUtils.getTemporal(tr.getYear(), tr.getMonth(), tr.getDay())
//                                .ifPresent(x -> setField(doc,"eventDateSingle", x));
//                    }
//                } catch (ParseException e){
//                    //ignore for now
//                }

                //GBIF taxonomy - add if available
                if (txr != null) {
                    //add the classification
                    List<RankedName> taxonomy = txr.getClassification();
                    for (RankedName entry : taxonomy) {
                        setField(doc,"gbif_s_" + entry.getRank().toString().toLowerCase() + "_id", entry.getKey());
                        setField(doc,"gbif_s_" + entry.getRank().toString().toLowerCase(), entry.getName());
                    }

                    String rank = txr.getAcceptedUsage().getRank().toString();
                    setField(doc,"gbif_s_rank", txr.getAcceptedUsage().getRank().toString());
                    setField(doc,"gbif_s_scientificName", txr.getAcceptedUsage().getName().toString());
                }

                //Verbatim (Raw) data
                Map<String,String> raw = er.getCoreTerms();
                for (Map.Entry<String, String> entry : raw.entrySet()) {
                    String key = entry.getKey();
                    if(key.startsWith("http")){
                        key = key.substring(key.lastIndexOf("/") + 1);
                    }
                    setField(doc,"raw_" + key, entry.getValue().toString());
                }

                if(lr.getDecimalLatitude() != null && lr.getDecimalLongitude() != null){
                    addGeo(doc, lr.getDecimalLatitude(), lr.getDecimalLongitude());
                }

                //ALA taxonomy & species groups - backwards compatible for EYA
                if (atxr.getTaxonConceptID() != null){
                    List<Schema.Field> fields = atxr.getSchema().getFields();
                    for (Schema.Field field: fields){
                        Object value = atxr.get(field.name());
                        if (value != null && !field.name().equals("speciesGroup") && !field.name().equals("speciesSubgroup")){

                            if (field.name().equalsIgnoreCase("issues")){
                                setField(doc,"assertions", value.toString());
                            } else {
                                if (value instanceof Integer) {
                                    setField(doc,field.name(), (Integer) value);
                                } else {
                                    setField(doc,field.name(), value.toString());
                                }
                            }
                        }
                    }

                    //required for EYA
                    setField(doc,"names_and_lsid", atxr.getScientificName() + "|" + atxr.getTaxonConceptID() + "|" + atxr.getVernacularName() + "|" + atxr.getKingdom() + "|" + atxr.getFamily()); // is set to IGNORE in headerAttributes
                    setField(doc, "common_name_and_lsid",  atxr.getVernacularName() + "|" + atxr.getScientificName() + "|" + atxr.getTaxonConceptID() + "|" +  atxr.getVernacularName() + "|" + atxr.getKingdom() + "|" + atxr.getFamily()); // is set to IGNORE in headerAttributes

                    //legacy fields referenced in biocache-service code
                    setField(doc,"taxon_name", atxr.getScientificName());
                    setField(doc,"lsid", atxr.getTaxonConceptID());
                    setField(doc,"rank", atxr.getRank());
                    setField(doc,"rank_id", atxr.getRankID());

                    if(atxr.getVernacularName() != null) {
                        setField(doc,"common_name", atxr.getVernacularName());
                    }

                    for (String s : atxr.getSpeciesGroup()){
                        setField(doc,"species_group", s);
                    }
                    for (String s : atxr.getSpeciesSubgroup()){
                        setField(doc,"species_subgroup", s);
                    }
                }

                setField(doc,"geospatial_kosher", lr.getHasCoordinate());
                setField(doc,"first_loaded_date", new Date());

                if (asr != null) {
                    Map<String, String> samples = asr.getItems();
                    for (Map.Entry<String, String> sample : samples.entrySet()) {
                        if (!StringUtils.isEmpty(sample.getValue())) {
                            if (sample.getKey().startsWith("el")) {
                                setField(doc,sample.getKey(), Double.valueOf(sample.getValue()));
                            } else {
                                setField(doc,sample.getKey(), sample.getValue());
                            }
                        }
                    }
                }

                // Add legacy collectory fields
                if(aar != null){
                    addIfNotEmpty(doc,"license", aar.getLicenseType());
                    addIfNotEmpty(doc,"raw_dataResourceUid", aar.getDataResourceUid()); //for backwards compatibility
                    addIfNotEmpty(doc,"dataResourceUid", aar.getDataResourceUid());
                    addIfNotEmpty(doc,"dataResourceName", aar.getDataResourceName());
                    addIfNotEmpty(doc,"dataProviderUid", aar.getDataProviderUid());
                    addIfNotEmpty(doc,"dataProviderName", aar.getDataProviderName());
                    addIfNotEmpty(doc,"institutionUid", aar.getInstitutionUid());
                    addIfNotEmpty(doc,"collectionUid", aar.getCollectionUid());
                    addIfNotEmpty(doc,"institutionName", aar.getInstitutionName());
                    addIfNotEmpty(doc,"collectionName", aar.getCollectionName());
                }

                //legacy fields reference directly in biocache-service code
                if (txr != null) {
                    IssueRecord taxonomicIssues = txr.getIssues();
                    for(String issue : taxonomicIssues.getIssueList()){
                        setField(doc,"assertions", issue);
                    }
                }

                IssueRecord geospatialIssues = lr.getIssues();
                for (String issue : geospatialIssues.getIssueList()){
                    setField(doc,"assertions", issue);
                }

                IssueRecord temporalIssues = tr.getIssues();
                for (String issue : temporalIssues.getIssueList()){
                    setField(doc,"assertions", issue);
                }

                IssueRecord basisOfRecordIssues = br.getIssues();
                for (String issue : basisOfRecordIssues.getIssueList()){
                    setField(doc,"assertions", issue);
                }

                for (String issue : mdr.getIssues().getIssueList()){
                    setField(doc,"assertions", issue);
                }

                c.output(doc);

                counter.inc();
            }
        };

        return ParDo.of(fn).withSideInputs(metadataView);
    }

    void addIfNotEmpty(ALAIndexRecord doc, String fieldName, String value){
        if (StringUtils.isNotEmpty(value)){
            setField(doc, fieldName, value);
        }
    }

    void addGeo(ALAIndexRecord doc, double lat, double lon){
        String latlon = "";
        //ensure that the lat longs are in the required range before
        if (lat <= 90 && lat >= -90d && lon <= 180 && lon >= -180d) {
            //https://lucene.apache.org/solr/guide/7_0/spatial-search.html#indexing-points
            latlon = lat + "," + lon; //required format for indexing geodetic points in SOLR
        }

        setField(doc, "lat_long", latlon); // is set to IGNORE in headerAttributes
        setField(doc, "point-1", getLatLongString(lat, lon, "#")); // is set to IGNORE in headerAttributes
        setField(doc, "point-0.1", getLatLongString(lat, lon, "#.#")); // is set to IGNORE in headerAttributes
        setField(doc, "point-0.01", getLatLongString(lat, lon, "#.##")); // is set to IGNORE in headerAttributes
        setField(doc, "point-0.02", getLatLongStringStep(lat, lon, "#.##", 0.02)); // is set to IGNORE in headerAttributes
        setField(doc, "point-0.001", getLatLongString(lat, lon, "#.###")); // is set to IGNORE in headerAttributes
        setField(doc, "point-0.0001", getLatLongString(lat, lon, "#.####")); // is set to IGNORE in headerAttributes
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

    void addToDoc(SpecificRecordBase record, ALAIndexRecord doc, Set<String> skipKeys){

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
                                        setField(doc, f.name(), (Boolean) r);
                                        break;
                                    case FLOAT:
                                        setField(doc, f.name(), (Float) r);
                                        break;
                                    case DOUBLE:
                                        setField(doc, f.name(), (Double) r);
                                        break;
                                    case INT:
                                        setField(doc, f.name(), (Integer) r);
                                        break;
                                    case LONG:
                                        setField(doc, f.name(), (Long) r);
                                        break;
                                    default:
                                        setField(doc, f.name(), r.toString());
                                        break;
                                }
                            });
                        })
                );
    }

    void setField(ALAIndexRecord doc, String key, Date value){
//        if (value != null) doc.getDateProperties().put(key, value);
    }
    void setField(ALAIndexRecord doc, String key, Boolean value){
        if (value != null) doc.getBooleanProperties().put(key, value);
    }
    void setField(ALAIndexRecord doc, String key, String value){
        if (value != null) doc.getStringProperties().put(key, value);
    }
    void setField(ALAIndexRecord doc, String key, Float value){
        if (value != null) doc.getFloatProperties().put(key, value);
    }
    void setField(ALAIndexRecord doc, String key, Double value){
        if (value != null) doc.getDoubleProperties().put(key, value);
    }
    void setField(ALAIndexRecord doc, String key, Long value){
        if (value != null) doc.getLongProperties().put(key, value);
    }
    void setField(ALAIndexRecord doc, String key, Integer value){
        if (value != null) doc.getIntProperties().put(key, value);
    }
    void setField(ALAIndexRecord doc, String key, List<String> values){
        if (values != null) doc.getMultiValueProperties().put(key, values);
    }
}
