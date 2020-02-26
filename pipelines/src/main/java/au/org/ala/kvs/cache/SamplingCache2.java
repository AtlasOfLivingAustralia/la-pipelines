package au.org.ala.kvs.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.kvs.KeyValueStore;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;
import org.mapdb.serializer.SerializerCompressionWrapper;
import org.xerial.snappy.Snappy;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

/**
 * MapDB implementation of key value store.
 *
 */
public class SamplingCache2 {

    //Wrapped KeyValueStore

    private final DB db;

    private final HTreeMap<String, String> cache;

    public Map<String, String> getSamples(Double latitude, Double longitude) throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = cache.get(latitude + "," + longitude);
        return om.readValue(json, Map.class);
    }

    public void addToCache(Double latitude, Double longitude, Map<String, String> samples) throws Exception {
        ObjectMapper om = new ObjectMapper();
        cache.put(latitude + "," + longitude, om.writeValueAsString(samples));
    }

    /**
     * Creates a Cache for the KV store.
     */
    public SamplingCache2(String baseDirectory) {

        this.db = DBMaker
                .fileDB(baseDirectory + "/sample-cache")
                .closeOnJvmShutdown()
                .fileMmapEnableIfSupported()
                .fileMmapPreclearDisable()
                .make();

        cache = db.hashMap("sample-cache")
                .keySerializer(Serializer.STRING)
                .valueSerializer(new SerializerCompressionWrapper(Serializer.STRING))
            .createOrOpen();

        this.db.getStore().fileLoad();
    }


    public void closeForWriting(){
        db.commit();
    }
}
