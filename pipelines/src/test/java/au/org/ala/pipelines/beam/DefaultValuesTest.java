package au.org.ala.pipelines.beam;

import au.org.ala.pipelines.options.ALAInterpretationPipelineOptions;
import org.codehaus.plexus.util.FileUtils;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.pipelines.ingest.options.DwcaPipelineOptions;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.pipelines.DwcaToVerbatimPipeline;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.junit.Test;
import org.locationtech.jts.util.Assert;

import java.io.File;
import java.io.Serializable;
import java.util.function.Function;

/**
 * End to end default values test.
 */
public class DefaultValuesTest {

    @Test
    public void testDwCaPipeline() throws Exception {

        String testResourcePath = "src/test/resources";
        File file = new File(testResourcePath);
        String absolutePath = file.getAbsolutePath();

        DwcaPipelineOptions dwcaOptions = PipelinesOptionsFactory.create(DwcaPipelineOptions.class, new String[]{
                "--datasetId=dr893",
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=dwca-metrics.yml",
                "--targetPath=/tmp",
                "--inputPath=" + absolutePath + "/default-values/dr893"
        });
        DwcaToVerbatimPipeline.run(dwcaOptions);

        ALAInterpretationPipelineOptions interpretationOptions = PipelinesOptionsFactory.create(ALAInterpretationPipelineOptions.class, new String[]{
                "--datasetId=dr893",
                "--attempt=1",
                "--runner=SparkRunner",
                "--interpretationTypes=ALL",
                "--metaFileName=interpretation-metrics.yml",
                "--targetPath=/tmp",
                "--inputPath=/tmp/dr893/1/verbatim.avro",
                "--properties="+ testResourcePath +"/pipelines.properties",
                "--useExtendedRecordId=true",
                "--skipRegisrtyCalls=true"
        });
        ALAVerbatimToInterpretedPipeline.run(interpretationOptions);

        InterpretationPipelineOptions uuidOptions = PipelinesOptionsFactory.create(InterpretationPipelineOptions.class, new String[]{
                "--datasetId=dr893",
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=uuid-metrics.yml",
                "--targetPath=/tmp",
                "--inputPath=/tmp/dr893/1/verbatim.avro",
                "--properties="+ testResourcePath +"/pipelines.properties",
                "--useExtendedRecordId=true",
                "--skipRegisrtyCalls=true"
        });
        ALAUUIDMintingPipeline.run(uuidOptions);

        //check the original verbatim values are NOT populated with default values
        InterpretationPipelineOptions testOptions1 = PipelinesOptionsFactory.create(InterpretationPipelineOptions.class, new String[]{
                "--datasetId=dr893",
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=uuid-metrics.yml",
                "--targetPath=/tmp",
                "--inputPath=/tmp/dr893/1/verbatim.avro",
                "--properties="+ testResourcePath +"/pipelines.properties",
                "--useExtendedRecordId=true",
                "--skipRegisrtyCalls=true"
        });
        Function<ExtendedRecord, Boolean> notPopulated = (Function<ExtendedRecord, Boolean> & Serializable) er ->
                er.getCoreTerms().get(DwcTerm.basisOfRecord.namespace() + DwcTerm.basisOfRecord.name()) == null
                && er.getCoreTerms().get(DwcTerm.occurrenceStatus.namespace() + DwcTerm.basisOfRecord.name())  == null;
        AvroCheckPipeline.assertCountRecords(testOptions1, 5l, notPopulated);

        //check the interpreted values are populated with default values
        InterpretationPipelineOptions testOptions2 = PipelinesOptionsFactory.create(InterpretationPipelineOptions.class, new String[]{
                "--datasetId=dr893",
                "--attempt=1",
                "--runner=SparkRunner",
                "--metaFileName=uuid-metrics.yml",
                "--targetPath=/tmp",
                "--inputPath=/tmp/dr893/1/interpreted/verbatim/interpret-*",
                "--properties="+ testResourcePath +"/pipelines.properties",
                "--useExtendedRecordId=true",
                "--skipRegisrtyCalls=true"
        });
        Function<ExtendedRecord, Boolean> fullNameFunc = (Function<ExtendedRecord, Boolean> & Serializable) er ->
                er.getCoreTerms().get(DwcTerm.basisOfRecord.namespace() + DwcTerm.basisOfRecord.name()).equals("HumanObservation")
                && er.getCoreTerms().get(DwcTerm.occurrenceStatus.namespace() + DwcTerm.occurrenceStatus.name()).equals("present");
        AvroCheckPipeline.assertCountRecords(testOptions2, 5l, fullNameFunc);

        //cleanup test outputs
        FileUtils.forceDelete("/tmp/dr893");
    }
}
