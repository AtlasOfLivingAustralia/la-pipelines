package au.org.ala.pipelines.beam;

import au.org.ala.pipelines.options.ALASolrPipelineOptions;
import au.org.ala.pipelines.transforms.ALASolrDocumentTransform;
import au.org.ala.pipelines.transforms.ALATaxonomyTransform;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.solr.SolrIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.solr.common.SolrInputDocument;
import org.gbif.api.model.pipelines.StepType;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.utils.FsUtils;
import org.gbif.pipelines.ingest.utils.MetricsHandler;
import org.gbif.pipelines.io.avro.*;
import org.gbif.pipelines.transforms.core.*;
import org.gbif.pipelines.transforms.extension.AudubonTransform;
import org.gbif.pipelines.transforms.extension.ImageTransform;
import org.gbif.pipelines.transforms.extension.MeasurementOrFactTransform;
import org.gbif.pipelines.transforms.extension.MultimediaTransform;
import org.gbif.pipelines.transforms.specific.AustraliaSpatialTransform;
import org.slf4j.MDC;

import java.util.function.UnaryOperator;

import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.AVRO_EXTENSION;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ALAInterpretedToSolrIndexPipeline {

    public static void main(String[] args) {
        ALASolrPipelineOptions options = PipelinesOptionsFactory.create(ALASolrPipelineOptions.class, args);
        run(options);
    }

    public static void run(ALASolrPipelineOptions options) {

        MDC.put("datasetId", options.getDatasetId());
        MDC.put("attempt", options.getAttempt().toString());
        MDC.put("step", StepType.INTERPRETED_TO_INDEX.name());

        log.info("Adding step 1: Options");
        UnaryOperator<String> pathFn = t -> FsUtils.buildPathInterpretUsingTargetPath(options, t, "*" + AVRO_EXTENSION);

        Pipeline p = Pipeline.create(options);

        log.info("Adding step 2: Creating transformations");
        // Core
        BasicTransform basicTransform = BasicTransform.create();
        MetadataTransform metadataTransform = MetadataTransform.create();
        VerbatimTransform verbatimTransform = VerbatimTransform.create();
        TemporalTransform temporalTransform = TemporalTransform.create();
        TaxonomyTransform taxonomyTransform = TaxonomyTransform.create();
        ALATaxonomyTransform alaTaxonomyTransform = ALATaxonomyTransform.create();
        AustraliaSpatialTransform australiaSpatialTransform = AustraliaSpatialTransform.create();
        LocationTransform locationTransform = LocationTransform.create();

        // Extension
        MeasurementOrFactTransform measurementOrFactTransform = MeasurementOrFactTransform.create();
        MultimediaTransform multimediaTransform = MultimediaTransform.create();
        AudubonTransform audubonTransform = AudubonTransform.create();
        ImageTransform imageTransform = ImageTransform.create();

        log.info("Adding step 3: Creating beam pipeline");
        PCollectionView<MetadataRecord> metadataView =
                p.apply("Read Metadata", metadataTransform.read(pathFn))
                        .apply("Convert to view", View.asSingleton());

        PCollection<KV<String, ExtendedRecord>> verbatimCollection =
                p.apply("Read Verbatim", verbatimTransform.read(pathFn))
                        .apply("Map Verbatim to KV", verbatimTransform.toKv());

        PCollection<KV<String, BasicRecord>> basicCollection =
                p.apply("Read Basic", basicTransform.read(pathFn))
                        .apply("Map Basic to KV", basicTransform.toKv());

        PCollection<KV<String, TemporalRecord>> temporalCollection =
                p.apply("Read Temporal", temporalTransform.read(pathFn))
                        .apply("Map Temporal to KV", temporalTransform.toKv());

        PCollection<KV<String, LocationRecord>> locationCollection =
                p.apply("Read Location", locationTransform.read(pathFn))
                        .apply("Map Location to KV", locationTransform.toKv());

        PCollection<KV<String, TaxonRecord>> taxonCollection = null;
        if (options.getIncludeGbifTaxonomy()) {
            taxonCollection =
                    p.apply("Read Taxon", taxonomyTransform.read(pathFn))
                            .apply("Map Taxon to KV", taxonomyTransform.toKv());
        }

        PCollection<KV<String, ALATaxonRecord>> alaTaxonCollection =
                p.apply("Read Taxon", alaTaxonomyTransform.read(pathFn))
                        .apply("Map Taxon to KV", alaTaxonomyTransform.toKv());

        PCollection<KV<String, MultimediaRecord>> multimediaCollection =
                p.apply("Read Multimedia", multimediaTransform.read(pathFn))
                        .apply("Map Multimedia to KV", multimediaTransform.toKv());

        PCollection<KV<String, ImageRecord>> imageCollection =
                p.apply("Read Image", imageTransform.read(pathFn))
                        .apply("Map Image to KV", imageTransform.toKv());

        PCollection<KV<String, AudubonRecord>> audubonCollection =
                p.apply("Read Audubon", audubonTransform.read(pathFn))
                        .apply("Map Audubon to KV", audubonTransform.toKv());

        PCollection<KV<String, MeasurementOrFactRecord>> measurementCollection =
                p.apply("Read Measurement", measurementOrFactTransform.read(pathFn))
                        .apply("Map Measurement to KV", measurementOrFactTransform.toKv());

        PCollection<KV<String, AustraliaSpatialRecord>> australiaSpatialCollection = null;
        if(options.getIncludeSampling()) {
            australiaSpatialCollection =
                    p.apply("Read Sampling", australiaSpatialTransform.read(pathFn))
                            .apply("Map Sampling to KV", australiaSpatialTransform.toKv());
        }

        log.info("Adding step 3: Converting into a json object");
        ParDo.SingleOutput<KV<String, CoGbkResult>, SolrInputDocument> alaSolrDoFn =
                ALASolrDocumentTransform.create(
                        verbatimTransform.getTag(),
                        basicTransform.getTag(),
                        temporalTransform.getTag(),
                        locationTransform.getTag(),
                        options.getIncludeGbifTaxonomy() ? taxonomyTransform.getTag() : null,
                        alaTaxonomyTransform.getTag(),
                        multimediaTransform.getTag(),
                        imageTransform.getTag(),
                        audubonTransform.getTag(),
                        measurementOrFactTransform.getTag(),
                        options.getIncludeSampling() ? australiaSpatialTransform.getTag() : null,
                        metadataView
                ).converter();

        KeyedPCollectionTuple<String> kpct = KeyedPCollectionTuple
                // Core
                .of(basicTransform.getTag(), basicCollection)
                .and(temporalTransform.getTag(), temporalCollection)
                .and(locationTransform.getTag(), locationCollection)

                // Extension
                .and(multimediaTransform.getTag(), multimediaCollection)
                .and(imageTransform.getTag(), imageCollection)
                .and(audubonTransform.getTag(), audubonCollection)
                .and(measurementOrFactTransform.getTag(), measurementCollection)
                // Raw
                .and(verbatimTransform.getTag(), verbatimCollection)
                //Specific
                .and(alaTaxonomyTransform.getTag(), alaTaxonCollection);


        if (options.getIncludeSampling()){
            kpct = kpct.and(australiaSpatialTransform.getTag(), australiaSpatialCollection);
        }

        if (options.getIncludeGbifTaxonomy()){
            kpct = kpct.and(taxonomyTransform.getTag(), taxonCollection);
        }

        PCollection<SolrInputDocument> jsonCollection = kpct
                .apply("Grouping objects", CoGroupByKey.create())
                .apply("Merging to Solr doc", alaSolrDoFn);


        log.info("Adding step 4: SOLR indexing");
        SolrIO.ConnectionConfiguration conn = SolrIO.ConnectionConfiguration.create(
                options.getZkHost() //"localhost:9983"
        );

        jsonCollection.apply(
                SolrIO.write()
                        .to(options.getSolrCollection()) //biocache
                        .withConnectionConfiguration(conn)
        );

        log.info("Running the pipeline");
        PipelineResult result = p.run();
        result.waitUntilFinish();

        MetricsHandler.saveCountersToTargetPathFile(options, result.metrics());

        log.info("Pipeline has been finished");
    }
}
