package au.org.ala.util;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.gbif.pipelines.ingest.java.io.AvroReader;
import org.gbif.pipelines.io.avro.ALAUUIDRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities to support tests.
 */
public class TestUtils {

    public static final String BIOCACHE_TEST_SOLR_COLLECTION = "biocache_test";

    public static Map<String, String> readKeysForPath(String path){
        Map<String, ALAUUIDRecord> records = AvroReader.readRecords("", ALAUUIDRecord.class, path);
        Map<String, String> uniqueKeyToUuid = new HashMap<String, String>();
        for (Map.Entry<String, ALAUUIDRecord> record: records.entrySet()){
            System.out.println(record.getValue().getUniqueKey() + " -> " + record.getValue().getUuid());
            uniqueKeyToUuid.put(record.getValue().getUniqueKey(), record.getValue().getUuid());
        }
        return uniqueKeyToUuid;
    }

    public static void dumpKeysForPath(String path){
        Map<String, ALAUUIDRecord> records = AvroReader.readRecords("", ALAUUIDRecord.class, path);
        Map<String, String> uniqueKeyToUuid = new HashMap<String, String>();
        for (Map.Entry<String, ALAUUIDRecord> record: records.entrySet()){
            System.out.println(record.getValue().getUniqueKey() + " -> " + record.getValue().getUuid());
        }
    }

    public static void setupIndex() throws Exception {
        try {
            deleteSolrIndex();
        } catch (Exception e){
            //expected for new setups
        }
        createSolrIndex();
    }

    public static void createSolrIndex() throws Exception {


        final SolrClient cloudSolrClient = new CloudSolrClient("localhost:9983");
        final String solrZKConfigName = "biocache";

        final CollectionAdminRequest.Create adminRequest = new CollectionAdminRequest.Create();
        adminRequest.setConfigName(solrZKConfigName)
                .setCollectionName(BIOCACHE_TEST_SOLR_COLLECTION)
                .setNumShards(1)
                .setReplicationFactor(1);

        CollectionAdminResponse adminResponse = adminRequest.process(cloudSolrClient);

        cloudSolrClient.close();
    }

    public static void deleteSolrIndex() throws Exception {

        final SolrClient cloudSolrClient = new CloudSolrClient("localhost:9983");

        final CollectionAdminRequest.Delete adminRequest = new CollectionAdminRequest.Delete();
        adminRequest.setCollectionName(BIOCACHE_TEST_SOLR_COLLECTION);

        CollectionAdminResponse adminResponse = adminRequest.process(cloudSolrClient);

        cloudSolrClient.close();
    }

    public static void reloadSolrIndex() throws Exception {

        final SolrClient cloudSolrClient = new CloudSolrClient("localhost:9983");
        final CollectionAdminRequest.Reload adminRequest = new CollectionAdminRequest.Reload();
        adminRequest.setCollectionName(BIOCACHE_TEST_SOLR_COLLECTION);

        CollectionAdminResponse adminResponse = adminRequest.process(cloudSolrClient);

        cloudSolrClient.close();
    }

    public static Long getRecordCount(String queryUrl) throws Exception {

        String zkHost = "localhost:9983";
        String defaultCollection = "biocache_test";
        CloudSolrServer solr = new CloudSolrServer(zkHost);
        solr.setDefaultCollection(defaultCollection);

        SolrQuery params = new SolrQuery();
        params.setQuery(queryUrl);
        params.setSort("score ", SolrQuery.ORDER.desc);
        params.setStart(Integer.getInteger("0"));
        params.setRows(Integer.getInteger("100"));

        QueryResponse response = solr.query(params);
        SolrDocumentList results = response.getResults();
        return results.getNumFound();
    }

    public static SolrDocumentList getRecords(String queryUrl) throws Exception {

        String zkHost = "localhost:9983";
        String defaultCollection = "biocache_test";
        CloudSolrServer solr = new CloudSolrServer(zkHost);
        solr.setDefaultCollection(defaultCollection);

        SolrQuery params = new SolrQuery();
        params.setQuery(queryUrl);
        params.setSort("score ", SolrQuery.ORDER.desc);
        params.setStart(Integer.getInteger("0"));
        params.setRows(Integer.getInteger("100"));

        QueryResponse response = solr.query(params);
        SolrDocumentList results = response.getResults();
        return results;
    }
}
