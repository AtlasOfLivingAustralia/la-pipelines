package au.org.ala.pipelines.beam;

import au.org.ala.pipelines.options.ALASolrPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.io.solr.SolrIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.solr.common.SolrInputDocument;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.io.avro.AustraliaSpatialRecord;

import java.util.Map;
import java.util.UUID;

public class AvroSolrTest {

    public static void main(String[] args){

        ALASolrPipelineOptions options = PipelinesOptionsFactory.create(ALASolrPipelineOptions.class, args);
        Pipeline p = Pipeline.create(options);
        PCollection<SolrInputDocument> records =
                p.apply(AvroIO.read(AustraliaSpatialRecord.class).from("/data/pipelines-data/" + options.getDatasetId().trim() + "/1/interpreted/australia_spatial/*.avro"))
                .apply(ParDo.of(new SolrDocumentFn()));
        SolrIO.ConnectionConfiguration conn = SolrIO.ConnectionConfiguration.create(
                options.getZkHost()
        );
        records.apply(
                SolrIO.write()
                        .to(options.getSolrCollection())
                        .withConnectionConfiguration(conn)
        );
        PipelineResult result = p.run();
        result.waitUntilFinish();
    }

    static class SolrDocumentFn extends DoFn<AustraliaSpatialRecord, SolrInputDocument> {

        @ProcessElement
        public void processElement(@Element AustraliaSpatialRecord l, OutputReceiver<SolrInputDocument> outputReceiver) {

            SolrInputDocument s = new SolrInputDocument();
            s.setField("id", UUID.randomUUID().toString());
            int count = 0;
            for (Map.Entry entry: l.getItems().entrySet()){
                count++;
                //double up fields
                s.setField(entry.getKey().toString(), entry.getValue().toString());
                if(count== 100)
                    break;
            }
            outputReceiver.output(s);
        }
    }
}
