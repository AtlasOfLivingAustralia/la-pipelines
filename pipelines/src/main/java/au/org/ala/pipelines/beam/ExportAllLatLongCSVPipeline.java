package au.org.ala.pipelines.beam;

import au.org.ala.pipelines.transforms.ALACSVDocumentTransform;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.Distinct;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.commons.io.FileUtils;
import org.gbif.pipelines.ingest.options.BasePipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.utils.FsUtils;
import org.gbif.pipelines.ingest.utils.MetricsHandler;
import org.gbif.pipelines.io.avro.LocationRecord;
import org.gbif.pipelines.transforms.core.LocationTransform;
import org.slf4j.MDC;

import java.io.File;
import java.util.function.UnaryOperator;

import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.AVRO_EXTENSION;

/**
 * Exports a unique set of coordinates for a data resource.
 * This tool is dependent on globstars being supported on the host OS.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportAllLatLongCSVPipeline {

    public static void main(String[] args) throws Exception {
        AllDatasetsPipelinesOptions options = PipelinesOptionsFactory.create(AllDatasetsPipelinesOptions.class, args);
        run(options);
    }

    public static void run(AllDatasetsPipelinesOptions options) throws Exception {

        FileUtils.forceMkdir(new File("/data/pipelines-sampling/latlng/"));

        log.info("Adding step 1: Options");
        UnaryOperator<String> pathFn = t -> "/data/pipelines-data/**/1/interpreted/" + t + "/interpret-*" + AVRO_EXTENSION;

        Pipeline p = Pipeline.create(options);

        log.info("Adding step 2: Creating transformations");

        // Core
        LocationTransform locationTransform = LocationTransform.create();

        log.info("Adding step 3: Creating beam pipeline");
        PCollection<KV<String, LocationRecord>> locationCollection =
                p.apply("Read Location", locationTransform.read(pathFn))
                        .apply("Map Location to KV", locationTransform.toKv());

        log.info("Adding step 3: Converting into a json object");
        ParDo.SingleOutput<KV<String, CoGbkResult>, String> alaCSVrDoFn =
                ALACSVDocumentTransform.create(locationTransform.getTag()).converter();

        PCollection<String> csvCollection =
                KeyedPCollectionTuple
                        .of(locationTransform.getTag(), locationCollection)
                        .apply("Grouping objects", CoGroupByKey.create())
                        .apply("Merging to CSV doc", alaCSVrDoFn);

        csvCollection
                .apply(Distinct.<String>create())
                .apply(TextIO.write().to("/data/pipelines-sampling/latlng/latlong.csv"));

        log.info("Running the pipeline");
        PipelineResult result = p.run();
        result.waitUntilFinish();

        log.info("Pipeline has been finished. Output written to /data/pipelines-sampling/latlng/latlng-export.csv");
    }
}