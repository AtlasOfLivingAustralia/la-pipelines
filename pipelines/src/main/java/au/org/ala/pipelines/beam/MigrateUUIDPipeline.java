package au.org.ala.pipelines.beam;

import au.com.bytecode.opencsv.CSVReader;
import au.org.ala.pipelines.common.ALARecordTypes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.CodecFactory;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.gbif.pipelines.common.PipelinesVariables;
import org.gbif.pipelines.ingest.options.BasePipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.io.avro.ALAUUIDRecord;

import java.io.StringReader;

import static org.apache.beam.sdk.io.FileIO.Write.defaultNaming;

/**
 * A temporary pipeline used for data migration. This uses a extract from the cassandra occ_uuid and generates
 * the necessary files in AVRO and distributes them for each dataset.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MigrateUUIDPipeline {

    private static final CodecFactory BASE_CODEC = CodecFactory.snappyCodec();
    public static void main(String[] args) throws Exception {
        BasePipelineOptions options = PipelinesOptionsFactory.create(BasePipelineOptions.class, args);
        run(options);
    }

    public static void run(BasePipelineOptions options) throws Exception {

        // Initialise pipeline
        Pipeline p = Pipeline.create(options);

        // read data into a KV structure UniqueKey -> UUID
        PCollection<KV<String, ALAUUIDRecord>> records = p.apply(TextIO.read().from(options.getInputPath())).apply(ParDo.of(new StringToDatasetIDAvroRecordFcn()));

        //write out into AVRO in each separate directory
        records.apply("Write avro file per dataset", FileIO.<String, KV<String, ALAUUIDRecord>>writeDynamic()
                .by(KV::getKey)
                .via(Contextful.fn(KV::getValue), Contextful.fn(x -> AvroIO.sink(ALAUUIDRecord.class).withCodec(BASE_CODEC)))
                .to(options.getTargetPath())
                .withDestinationCoder(StringUtf8Coder.of())
                .withNaming(key -> defaultNaming(key + "/1/identifiers/" + ALARecordTypes.ALA_UUID.toString().toLowerCase() + "/interpret", PipelinesVariables.Pipeline.AVRO_EXTENSION)));

        PipelineResult result = p.run();
        result.waitUntilFinish();
    }

    /**
     * Function to create ALAUUIDRecords.
     */
    static class StringToDatasetIDAvroRecordFcn extends DoFn<String, KV<String, ALAUUIDRecord>> {

        @ProcessElement
        public void processElement(@Element String line, OutputReceiver<KV<String, ALAUUIDRecord>> out) {
            try {
                CSVReader csvReader = new CSVReader(new StringReader(line));
                String[] fields = csvReader.readNext();

                String datasetID = fields[0].substring(0, fields[0].indexOf("|"));

                ALAUUIDRecord record = ALAUUIDRecord.newBuilder().setId(fields[1]).setUniqueKey(fields[0]).setUuid(fields[1]).build();

                KV<String, ALAUUIDRecord> kv = KV.of(datasetID,record);
                out.output(kv);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
//






//        // Use pre-processed coordinates from location transform outputs
//        log.info("Adding step 2: Initialise location transform");
//        VerbatimTransform verbatimTransform = VerbatimTransform.create();
//
//        log.info("Adding step 3: Creating beam pipeline");
//        PCollection<KV<String, LocationRecord>> locationCollection =
//                p.apply("Read Location", locationTransform.read(pathFn))
//                        .apply("Map Location to KV", locationTransform.toKv());
//
//        log.info("Adding step 3: Converting into a CSV object");
//        ParDo.SingleOutput<KV<String, CoGbkResult>, String> alaCSVrDoFn =
//                ALACSVDocumentTransform.create(locationTransform.getTag()).converter();
//
//        PCollection<String> csvCollection =
//                KeyedPCollectionTuple
//                        .of(locationTransform.getTag(), locationCollection)
//                        .apply("Grouping objects", CoGroupByKey.create())
//                        .apply("Merging to CSV doc", alaCSVrDoFn);
//
//        String outputPath = FsUtils.buildDatasetAttemptPath(options, "latlng", true);
//
//        log.info("Output path = " + outputPath);
//        FileUtils.forceMkdir(new File(outputPath));
//
//        csvCollection
//                .apply(Distinct.<String>create())
//                .apply(TextIO.write().to(outputPath + "/latlong.csv"));
//
//        log.info("Running the pipeline");
//        PipelineResult result = p.run();
//        result.waitUntilFinish();
//
//        MetricsHandler.saveCountersToTargetPathFile(options, result.metrics());
//
//        log.info("Pipeline has been finished. Output written to " + outputPath + "/latlong.csv");
