package au.org.ala.pipelines.beam;

import au.org.ala.utils.CombinedYamlConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.gbif.api.model.pipelines.StepType;
import org.gbif.pipelines.common.PipelinesVariables;
import org.gbif.pipelines.common.beam.DwcaIO;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.utils.FsUtils;
import org.gbif.pipelines.ingest.utils.MetricsHandler;
import org.gbif.pipelines.transforms.core.VerbatimTransform;
import org.slf4j.MDC;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

/**
 * FIXME: This class has been temporarily copied from gbif/pipelines to add the use of InterpretationPipelineOptions
 * to test with HDFS.
 */
@Slf4j
public class DwcaToVerbatimPipeline {

    public static void main(String[] args) throws FileNotFoundException {
       String[] combinedArgs = new CombinedYamlConfiguration(args).toArgs("general", "dwca-avro");
       InterpretationPipelineOptions options =
           PipelinesOptionsFactory.createInterpretation(combinedArgs);
       run(options);
    }

    public static void run(InterpretationPipelineOptions options) {

        MDC.put("datasetId", options.getDatasetId());
        MDC.put("attempt", options.getAttempt().toString());
        MDC.put("step", StepType.DWCA_TO_VERBATIM.name());

        log.info("Adding step 1: Options");
        String inputPath = options.getInputPath();
        String targetPath = FsUtils.buildDatasetAttemptPath(options, PipelinesVariables.Pipeline.Conversion.FILE_NAME, false);

        String tmpPath = FsUtils.getTempDir(options);

        boolean isDir = Paths.get(inputPath).toFile().isDirectory();

        DwcaIO.Read reader = isDir ? DwcaIO.Read.fromLocation(inputPath) : DwcaIO.Read.fromCompressed(inputPath, tmpPath);

        log.info("Adding step 2: Pipeline steps");
        Pipeline p = Pipeline.create(options);

        p.apply("Read from Darwin Core Archive", reader)
                .apply("Write to avro", VerbatimTransform.create().write(targetPath).withoutSharding());

        log.info("Running the pipeline");
        PipelineResult result = p.run();
        result.waitUntilFinish();

        MetricsHandler.saveCountersToTargetPathFile(options, result.metrics());

        log.info("Pipeline has been finished");
    }
}