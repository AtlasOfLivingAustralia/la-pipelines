package au.org.ala.kvs.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.plexus.util.FileUtils;
import org.gbif.kvs.KeyValueStore;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;
import org.mapdb.serializer.SerializerCompressionWrapper;
import org.xerial.snappy.Snappy;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MapDB implementation of a sampling cache.
 */
public class SamplingCache {

    private final DB db;

    private final HTreeMap<String, String> cache;

    public Map<String, String> getSamples(Double latitude, Double longitude) throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = cache.get(latitude + "," + longitude);
        if (json == null) {
            return new HashMap<String, String>();
        }
        return om.readValue(json, Map.class);
    }

    public void addToCache(Double latitude, Double longitude, Map<String, String> samples) throws Exception {
        ObjectMapper om = new ObjectMapper();
        cache.put(latitude + "," + longitude, om.writeValueAsString(samples));
    }

    /**
     * Create a new cache, removing the existing cache if it exists.
     * @param baseDirectory
     * @param cacheFileName
     * @return
     * @throws IOException
     */
    public static SamplingCache createNewCache(String baseDirectory, String cacheFileName) throws IOException {
        File existing = new File(baseDirectory + "/" + cacheFileName);
        if (existing.exists()){
            FileUtils.forceDelete(existing);
        }

        return new SamplingCache(baseDirectory, cacheFileName, false);
    }

    /**
     * Open an existing cache for readonly access.
     * @param baseDirectory
     * @param cacheFileName
     * @return
     * @throws IOException
     */
    public static SamplingCache openReadOnlyCache(String baseDirectory, String cacheFileName) throws IOException {
        File existing = new File(baseDirectory + "/" + cacheFileName);
        if (!existing.exists()){
            throw new IOException("Cache not found at " + baseDirectory + "/" + cacheFileName);
        }
        return new SamplingCache(baseDirectory, cacheFileName, true);
    }

    /**
     * Create a cache in readonly mode, or for writing.
     */
    private SamplingCache(String baseDirectory, String cacheFileName, Boolean readonly) {

        if(readonly){
            this.db = DBMaker
                    .fileDB(baseDirectory + "/" + cacheFileName)
                    .concurrencyDisable()
                    .readOnly()
                    .make();
        } else {
            this.db = DBMaker
                    .fileDB(baseDirectory + "/" + cacheFileName)
                    .closeOnJvmShutdown()
                    .fileMmapEnableIfSupported()
                    .fileMmapPreclearDisable()
                    .make();
        }

        this.cache = db.hashMap(cacheFileName)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new SerializerCompressionWrapper(Serializer.STRING))
            .createOrOpen();

        this.db.getStore().fileLoad();
    }

    public void closeForWriting(){
        this.db.commit();
        this.db.close();
    }
}
