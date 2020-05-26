package au.org.ala.pipelines.beam;

import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.junit.Test;

import java.io.File;

public class MIgrationPipelineTest {

    @Test
    public void testMigration(){
        String absolutePath = new File("src/test/resources").getAbsolutePath();
        InterpretationPipelineOptions options = PipelinesOptionsFactory.create(InterpretationPipelineOptions.class, new String[]{
                "--datasetId=ALL",
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=uuid-metrics.yml",
                "--targetPath=/tmp/la-pipelines-test/uuid-migration",
                "--inputPath=" + absolutePath + "/uuid-migration/occ_uuid.csv",
                "--properties=src/test/resources/pipelines.properties"
        });
        MigrateUUIDPipeline.run(options);
    }
}
