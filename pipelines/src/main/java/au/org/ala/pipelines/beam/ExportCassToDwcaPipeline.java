package au.org.ala.pipelines.beam;

import au.com.bytecode.opencsv.CSVReader;
import au.org.ala.pipelines.common.ALARecordTypes;
import au.org.ala.pipelines.model.CassandraOccurrence;
import au.org.ala.pipelines.model.CassandraQid;
import au.org.ala.pipelines.options.ALAExportPipelineOptions;
import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.CodecFactory;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.io.Compression;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.hadoop.hdfs.tools.CacheAdmin;
import org.gbif.pipelines.common.PipelinesVariables;
import org.gbif.pipelines.ingest.options.BasePipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.io.avro.ALAUUIDRecord;

import javax.xml.soap.Text;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.List;

import static org.apache.beam.sdk.io.FileIO.Write.defaultNaming;

/**
 * A temporary pipeline used for data migration. This uses a extract from the cassandra occ_uuid and generates
 * the <DATASET_ID>/1/identifiers/ala_uuid files in AVRO and distributes them for each dataset into separate
 * directories.
 * <p>
 * This class should not be part of the codebase in the long term and should be removed eventually.
 * <p>
 * Note: this pipeline can currently only be ran with the DirectRunner due to issues with SparkRunner
 * logged here: https://jira.apache.org/jira/browse/BEAM-10100
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportCassToDwcaPipeline {

    private static final CodecFactory BASE_CODEC = CodecFactory.snappyCodec();

    public static void main(String[] args) throws Exception {
        ALAExportPipelineOptions options = PipelinesOptionsFactory.create(ALAExportPipelineOptions.class, args);
        run(options);
    }

    public static void run(ALAExportPipelineOptions options) {

        // Initialise pipeline
        Pipeline p = Pipeline.create(options);

//        PCollection<String> records = p.apply(CassandraIO.<CassandraQid>read()
//                .withHosts(Arrays.asList(options.getCassandraHosts().split(",")))
//                .withPort(options.getPort())
//                .withKeyspace(options.getDatabase())
//                .withTable(options.getTable())
//                .withEntity(CassandraQid.class)
//                .withConsistencyLevel("ONE")
//                .withCoder(SerializableCoder.of(CassandraQid.class)
//                ))
//                .apply(ParDo.of(new DoFn<CassandraQid, String>() {
//                    @ProcessElement
//                    public void processElement(ProcessContext c) {
//
//                        CassandraQid r = c.element();
//                        //get the matched ExtendedRecord.getId()
//                        log.info("tested");
//                    }
//                }));

        PCollection<CassandraOccurrence> records = p.apply(CassandraIO.<CassandraOccurrence>read()
                .withHosts(Arrays.asList(options.getCassandraHosts()))
                .withPort(options.getPort())
                .withKeyspace(options.getDatabase())
                .withTable(options.getTable())
                .withEntity(CassandraOccurrence.class)
                .withCoder(SerializableCoder.of(CassandraOccurrence.class)
                ));

         records.apply(
                FileIO.<String, CassandraOccurrence>writeDynamic()
                        .by(CassandraOccurrence::getDataResourceUid)
                        .withCompression(Compression.ZIP)
                        .via(occ -> new CSVSink(occ))
                        .to(options.getExportFolder())
                        .withNaming(dr -> defaultNaming(dr+"", ".csv")))
                        .withNumShards(1);

        PipelineResult result = p.run();
        result.waitUntilFinish();
    }

    static class CSVSink implements FileIO.Sink<CassandraOccurrence> {
        private String header;
        private PrintWriter writer;

        public CSVSink(CassandraOccurrence occurrence) {
            this.header = Joiner.on(",").join(occurrence.getFieldNames());
        }

        public void open(WritableByteChannel channel) throws IOException {
            writer = new PrintWriter(Channels.newOutputStream(channel));
            writer.println(header);
        }

        public void write(CassandraOccurrence occurrence) throws IOException {
            writer.println(Joiner.on(",").join(occurrence.mapDwca().values()));
        }

        public void flush() throws IOException {
            writer.flush();
        }
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

                KV<String, ALAUUIDRecord> kv = KV.of(datasetID, record);
                out.output(kv);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage() + " - problem ID: " + line);
            }
        }
    }
}