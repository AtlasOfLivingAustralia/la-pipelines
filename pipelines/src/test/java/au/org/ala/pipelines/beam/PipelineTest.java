package au.org.ala.pipelines.beam;

import au.org.ala.pipelines.options.ALAInterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.DwcaPipelineOptions;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.pipelines.DwcaToVerbatimPipeline;
import org.junit.Test;

public class PipelineTest {

    @Test
    public void testDwCaPipeline() throws Exception {

        System.out.println("############## Load DWCA -> VERBATIM");
        DwcaPipelineOptions dwcaOptions = PipelinesOptionsFactory.create(DwcaPipelineOptions.class, new String[]{
                "--datasetId=dr893",
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=dwca-metrics.yml",
                "--targetPath=/tmp",
                "--inputPath=/Users/mar759/dev/la-pipelines/pipelines/src/test/resources/dr893"
        });
        DwcaToVerbatimPipeline.run(dwcaOptions);

        System.out.println("############## VERBATIM -> INTERPRETED");
        ALAInterpretationPipelineOptions interpretationOptions = PipelinesOptionsFactory.create(ALAInterpretationPipelineOptions.class, new String[]{
                "--datasetId=dr893",
                "--attempt=1",
                "--runner=SparkRunner",
                "--interpretationTypes=ALL",
                "--metaFileName=interpretation-metrics.yml",
                "--targetPath=/tmp",
                "--inputPath=/tmp/dr893/1/verbatim.avro",
                "--properties=/Users/mar759/dev/la-pipelines/pipelines.properties",
                "--useExtendedRecordId=true",
                "--skipRegisrtyCalls=true"
        });
        ALAVerbatimToInterpretedPipeline.run(interpretationOptions);

        System.out.println("############## INTERPRETED -> UUID");
        InterpretationPipelineOptions uuidOptions = PipelinesOptionsFactory.create(InterpretationPipelineOptions.class, new String[]{
                "--datasetId=dr893",
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=uuid-metrics.yml",
                "--targetPath=/tmp",
                "--inputPath=/tmp/dr893/1/verbatim.avro",
                "--properties=/Users/mar759/dev/la-pipelines/pipelines.properties",
                "--useExtendedRecordId=true",
                "--skipRegisrtyCalls=true"
        });
        ALAUUIDMintingPipeline.run(uuidOptions);

    }
}
