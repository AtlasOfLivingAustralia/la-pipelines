package au.org.ala.pipelines.beam;

import au.org.ala.pipelines.transforms.ALACSVDocumentTransform;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.gbif.pipelines.ingest.options.BasePipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.utils.FsUtils;
import org.gbif.pipelines.ingest.utils.MetricsHandler;
import org.gbif.pipelines.io.avro.LocationRecord;
import org.gbif.pipelines.io.avro.MetadataRecord;
import org.gbif.pipelines.transforms.core.LocationTransform;
import org.gbif.pipelines.transforms.core.MetadataTransform;
import org.slf4j.MDC;

import java.util.function.UnaryOperator;

import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.AVRO_EXTENSION;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ALAInterpretedToLatLongCSVPipeline {

    public static void main(String[] args) {
        BasePipelineOptions options = PipelinesOptionsFactory.create(BasePipelineOptions.class, args);
        run(options);
    }

    public static void run(BasePipelineOptions options) {

        MDC.put("datasetId", options.getDatasetId());
        MDC.put("attempt", options.getAttempt().toString());

        log.info("Adding step 1: Options");
        UnaryOperator<String> pathFn = t -> FsUtils.buildPathInterpretUsingTargetPath(options, t, "*" + AVRO_EXTENSION);

        Pipeline p = Pipeline.create(options);

        log.info("Adding step 2: Creating transformations");

        // Core
        MetadataTransform metadataTransform = MetadataTransform.create();
        LocationTransform locationTransform = LocationTransform.create();

        log.info("Adding step 3: Creating beam pipeline");
        PCollectionView<MetadataRecord> metadataView =
                p.apply("Read Metadata", metadataTransform.read(pathFn))
                        .apply("Convert to view", View.asSingleton());

        PCollection<KV<String, LocationRecord>> locationCollection =
                p.apply("Read Location", locationTransform.read(pathFn))
                        .apply("Map Location to KV", locationTransform.toKv());

        log.info("Adding step 3: Converting into a json object");
        ParDo.SingleOutput<KV<String, CoGbkResult>, String> alaCSVrDoFn =
                ALACSVDocumentTransform.create(locationTransform.getTag(), metadataView).converter();

        PCollection<String> csvCollection =
                KeyedPCollectionTuple
                        .of(locationTransform.getTag(), locationCollection)
                        .apply("Grouping objects", CoGroupByKey.create())
                        .apply("Merging to CSV doc", alaCSVrDoFn);

        csvCollection.apply(TextIO.write().to(options.getTargetPath() +"/latlong.csv"));

        log.info("Running the pipeline");
        PipelineResult result = p.run();
        result.waitUntilFinish();

        MetricsHandler.saveCountersToTargetPathFile(options, result.metrics());

        log.info("Pipeline has been finished. Output written to " + options.getTargetPath() + "/latlong.csv");
    }
}
