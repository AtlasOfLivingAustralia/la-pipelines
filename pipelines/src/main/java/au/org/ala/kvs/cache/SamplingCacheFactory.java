package au.org.ala.kvs.cache;

import au.org.ala.kvs.client.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.gbif.kvs.KeyValueStore;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton for accessing Sample Caches
 */
@Slf4j
public class SamplingCacheFactory {

    static SamplingCacheFactory instance;

    Map<String, KeyValueStore<LatLng, Map<String, String>>> refCache =  new HashMap<String, KeyValueStore<LatLng, Map<String, String>>>();

    Map<String, SamplingCache> refCache2 =  new HashMap<String, SamplingCache>();

    private SamplingCacheFactory(){}

    public static SamplingCacheFactory getInstance() {
        if(instance == null){
            instance  = new SamplingCacheFactory();
        }
        return instance;
    }

    /**
     * Setup references for a sample cache for the supplied dataset.
     *
     * @param datasetID
     */
    public static void setupFor(String datasetID) {
        try {
            SamplingCache cache = openForDataset(datasetID);
            getInstance().refCache2.put(datasetID, cache);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new RuntimeException("Unable to add sampling dataset for " + datasetID);
        }
    }

    /**
     * Retrieve a dataset sample cache, reusing an opened readonly cache is available.
     *
     * @param datasetID
     * @return
     */
    public static SamplingCache getForDataset(String datasetID)  {
        SamplingCache sc =  getInstance().refCache2.get(datasetID);
        if (sc != null) {
            return sc;
        } else {
            try {
                sc = openForDataset(datasetID);
                getInstance().refCache2.put(datasetID, sc);
                return sc;
            } catch(Exception e){
                throw new RuntimeException("Unable to open sampling cache for datasetID " + datasetID + " - " + e.getMessage(), e);
            }
        }
    }

    private static SamplingCache openForDataset (String datasetID) throws Exception {
        return SamplingCache.openReadOnlyCache("/data/pipelines-data/" + datasetID + "/1/caches", "sample-cache");
    }
}
