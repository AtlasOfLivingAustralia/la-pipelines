package au.org.ala.pipelines.beam;

import au.org.ala.pipelines.transforms.ALASamplingTransform;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.gbif.api.model.pipelines.StepType;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.utils.FsUtils;
import org.gbif.pipelines.io.avro.*;
import org.gbif.pipelines.transforms.core.LocationTransform;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;
import java.util.function.UnaryOperator;

import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.AVRO_EXTENSION;

/**
 * Pipeline sequence:
 *
 * <pre>
 *    1) Reads verbatim.avro file
 *    2) Interprets and converts avro {@link ExtendedRecord} file to:
 *      {@link MetadataRecord},
 *      {@link BasicRecord},
 *      {@link TemporalRecord},
 *      {@link MultimediaRecord},
 *      {@link ImageRecord},
 *      {@link AudubonRecord},
 *      {@link MeasurementOrFactRecord},
 *      {@link TaxonRecord},
 *      {@link LocationRecord}
 *    3) Writes data to independent files
 * </pre>
 *
 * <p>How to run:
 *
 * <pre>{@code
 * java -jar target/ingest-gbif-standalone-BUILD_VERSION-shaded.jar some.properties
 *
 * or pass all parameters:
 *
 * java -jar target/ingest-gbif-standalone-BUILD_VERSION-shaded.jar
 * --pipelineStep=VERBATIM_TO_ALA_INTERPRETED \
 * --properties=/some/path/to/output/ws.properties
 * --datasetId=0057a720-17c9-4658-971e-9578f3577cf5
 * --attempt=1
 * --interpretationTypes=ALL
 * --runner=SparkRunner
 * --targetPath=/some/path/to/output/
 * --inputPath=/some/path/to/output/0057a720-17c9-4658-971e-9578f3577cf5/1/verbatim.avro
 *
 * }</pre>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ALASamplingPipeline {

  public static void main(String[] args) {
    InterpretationPipelineOptions options = PipelinesOptionsFactory.createInterpretation(args);
    run(options);
  }

  public static void run(InterpretationPipelineOptions options) {

    String datasetId = options.getDatasetId();
    Integer attempt = options.getAttempt();
    String targetPath = options.getTargetPath();
    String hdfsSiteConfig = options.getHdfsSiteConfig();
    Properties properties = FsUtils.readPropertiesFile(options.getHdfsSiteConfig(), options.getProperties());

    FsUtils.deleteInterpretIfExist(hdfsSiteConfig, targetPath, datasetId, attempt, options.getInterpretationTypes());

    MDC.put("datasetId", datasetId);
    MDC.put("attempt", attempt.toString());
    MDC.put("step", StepType.VERBATIM_TO_INTERPRETED.name());

    String id = Long.toString(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

    UnaryOperator<String> pathLocationFn = t -> FsUtils.buildPathInterpretUsingTargetPath(options, t, "*" + AVRO_EXTENSION);

    UnaryOperator<String> pathFn = t -> FsUtils.buildPathInterpretUsingTargetPath(options, t, id);

    log.info("Creating a pipeline from options");
    Pipeline p = Pipeline.create(options);

    log.info("Creating transformations");
    LocationTransform locationTransform = LocationTransform.create();
    ALASamplingTransform alaSamplingTransform = ALASamplingTransform.create(properties);

    log.info("Adding pipeline transforms");
    p.apply("Read Location", locationTransform.read(pathLocationFn))
            .apply("Interpret Sampling", alaSamplingTransform.interpret())
            .apply("Write sampling to avro", alaSamplingTransform.write(pathFn));

    log.info("Running the pipeline");
    PipelineResult result = p.run();
    result.waitUntilFinish();

    log.info("Deleting beam temporal folders");
    String tempPath = String.join("/", targetPath, datasetId, attempt.toString());
    FsUtils.deleteDirectoryByPrefix(hdfsSiteConfig, tempPath, ".temp-beam");

    log.info("Pipeline has been finished");
  }
}
