package au.org.ala.pipelines.beam;

import au.org.ala.pipelines.options.ALAInterpretationPipelineOptions;
import au.org.ala.util.TestUtils;
import org.codehaus.plexus.util.FileUtils;
import org.gbif.pipelines.ingest.options.DwcaPipelineOptions;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.pipelines.DwcaToVerbatimPipeline;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class UUIDPipelineTest {

    /**
     * Tests for UUID creation. This test simulates a dataset being:
     *
     * 1) Loaded
     * 2) Re-loaded
     * 3) Re-loaded with records removed
     * 4) Re-loaded with  removed records being added back & UUID being preserved.
     *
     * @throws Exception
     */
    @Test
    public void testUuidsPipeline() throws Exception {

        //clear up previous test runs
        FileUtils.forceDelete("/tmp/la-pipelines-test/uuid-management");

        String absolutePath = new File("src/test/resources").getAbsolutePath();

        // Step 1: load a dataset and verify all records have a UUID associated
        loadTestDataset("dr893", absolutePath + "/uuid-management/dr893");

        //validation function
        Map<String, String> keysAfterFirstRun = TestUtils.readKeysForPath("/tmp/la-pipelines-test/uuid-management/dr893/1/identifiers/ala_uuid/interpret-*");
        assert keysAfterFirstRun.size() == 5;

        // Step 2: Check UUIDs where preserved
        loadTestDataset("dr893", absolutePath + "/uuid-management/dr893");
        Map<String, String> keysAfterSecondRun = TestUtils.readKeysForPath("/tmp/la-pipelines-test/uuid-management/dr893/1/identifiers/ala_uuid/interpret-*");

        //validate
        assert keysAfterFirstRun.size() == keysAfterSecondRun.size();
        for (Map.Entry<String, String> key  : keysAfterFirstRun.entrySet()){
            assert keysAfterSecondRun.containsKey(key.getKey());
            assert keysAfterSecondRun.get(key.getKey()).equals(key.getValue());
        }

        // Step 3: Check UUIDs where preserved for the removed records
        loadTestDataset("dr893", absolutePath + "/uuid-management/dr893-reduced");
        Map<String, String> keysAfterThirdRun = TestUtils.readKeysForPath("/tmp/la-pipelines-test/uuid-management/dr893/1/identifiers/ala_uuid/interpret-*");
        //validate
        for (Map.Entry<String, String> key  : keysAfterThirdRun.entrySet()){
            assert keysAfterFirstRun.containsKey(key.getKey());
            assert keysAfterFirstRun.get(key.getKey()).equals(key.getValue());
        }

        // Step 4: Check UUIDs where preserved for the re-added records
        loadTestDataset("dr893", absolutePath + "/uuid-management/dr893-readded");
        Map<String, String> keysAfterFourthRun = TestUtils.readKeysForPath("/tmp/la-pipelines-test/uuid-management/dr893/1/identifiers/ala_uuid/interpret-*");
        assert keysAfterFourthRun.size() == 6;
        //validate
        for (Map.Entry<String, String> key  : keysAfterFirstRun.entrySet()){
            assert keysAfterFourthRun.containsKey(key.getKey());
            assert keysAfterFourthRun.get(key.getKey()).equals(key.getValue());
        }
    }

    public void loadTestDataset(String datasetID, String inputPath) throws Exception {

        DwcaPipelineOptions dwcaOptions = PipelinesOptionsFactory.create(DwcaPipelineOptions.class, new String[]{
                "--datasetId=" + datasetID,
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=dwca-metrics.yml",
                "--targetPath=/tmp/la-pipelines-test/uuid-management",
                "--inputPath=" + inputPath
        });
        DwcaToVerbatimPipeline.run(dwcaOptions);

        ALAInterpretationPipelineOptions interpretationOptions = PipelinesOptionsFactory.create(ALAInterpretationPipelineOptions.class, new String[]{
                "--datasetId=" + datasetID,
                "--attempt=1",
                "--runner=SparkRunner",
                "--interpretationTypes=ALL",
                "--metaFileName=interpretation-metrics.yml",
                "--targetPath=/tmp/la-pipelines-test/uuid-management",
                "--inputPath=/tmp/la-pipelines-test/uuid-management/dr893/1/verbatim.avro",
                "--properties=src/test/resources/pipelines.properties",
                "--useExtendedRecordId=true",
                "--skipRegisrtyCalls=true"
        });
        ALAVerbatimToInterpretedPipeline.run(interpretationOptions);

        InterpretationPipelineOptions uuidOptions = PipelinesOptionsFactory.create(InterpretationPipelineOptions.class, new String[]{
                "--datasetId=" + datasetID,
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=uuid-metrics.yml",
                "--targetPath=/tmp/la-pipelines-test/uuid-management",
                "--inputPath=/tmp/la-pipelines-test/uuid-management/dr893/1/verbatim.avro",
                "--properties=src/test/resources/pipelines.properties",
                "--useExtendedRecordId=true",
                "--skipRegisrtyCalls=true"
        });
        ALAUUIDMintingPipeline.run(uuidOptions);
//
//        ALASolrPipelineOptions solrOptions = PipelinesOptionsFactory.create(ALASolrPipelineOptions.class, new String[]{
//                "--datasetId=" + datasetID,
//                "--attempt=1",
//                "--runner=SparkRunner",
//                "--metaFileName=uuid-metrics.yml",
//                "--targetPath=/tmp/la-pipelines-test/uuid-management",
//                "--inputPath=/tmp/la-pipelines-test/uuid-management/dr893/1/verbatim.avro",
//                "--properties=src/test/resources/pipelines.properties",
//                "--zkHost=localhost:9983",
//                "--solrCollection=biocache",
//                "--includeSampling=false"
//        });
//
//        ALAInterpretedToSolrIndexPipeline.run(solrOptions);
    }
}
