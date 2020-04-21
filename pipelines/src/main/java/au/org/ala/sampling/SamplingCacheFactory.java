package au.org.ala.sampling;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton for accessing Sample Caches
 */
@Slf4j
public class SamplingCacheFactory {

    static SamplingCacheFactory instance;

    Map<String, SamplingCache> refCache =  new HashMap<String, SamplingCache>();

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
            getInstance().refCache.put(datasetID, cache);
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
        SamplingCache sc =  getInstance().refCache.get(datasetID);
        if (sc != null) {
            return sc;
        } else {
            try {
                sc = openForDataset(datasetID);
                getInstance().refCache.put(datasetID, sc);
                return sc;
            } catch(Exception e){
                throw new RuntimeException("Unable to open sampling cache for datasetID " + datasetID + " - " + e.getMessage(), e);
            }
        }
    }

    /**
     * Open the dataset specific level cache. If this is unavailable, look the all datasets cache.
     *
     * @param datasetID
     * @return
     * @throws RuntimeException if no cache is available.
     */
    private static SamplingCache openForDataset (String datasetID) throws Exception {
        String cachePath = "/data/pipelines-data/" + datasetID + "/1/caches";
        File f = new File(cachePath);
        if (f.exists()){
            return SamplingCache.openReadOnlyCache("/data/pipelines-data/" + datasetID + "/1/caches", "sample-cache");
        }
        String allDatasetsCachePath = "/data/pipelines-data/sample-cache-all/sample-cache";
        f = new File(allDatasetsCachePath);
        if (f.exists()){
            return SamplingCache.openReadOnlyCache(allDatasetsCachePath, "sample-cache");
        }
        throw new RuntimeException("Neither dataset level cache or all dataset caches where available.");
    }
}
