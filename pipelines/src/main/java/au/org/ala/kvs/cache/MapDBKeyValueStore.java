package au.org.ala.kvs.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.gbif.kvs.KeyValueStore;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;
import org.xerial.snappy.Snappy;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * MapDB implementation of key value store.
 *
 * @param <K>
 * @param <V>
 */
public class MapDBKeyValueStore<K,V> implements WarmableCache<K,V> {

    //Wrapped KeyValueStore
    private final KeyValueStore<K, V> keyValueStore;

    private final DB db;

    private final HTreeMap<K, V> cache;

    /**
     * Creates a Cache for the KV store.
     *
     * @param keyValueStore wrapped kv store
     * @param keyClass      type descriptor for the key elements
     * @param valueClass    type descriptor for the value elements
     */
    private MapDBKeyValueStore(String baseDirectory, KeyValueStore<K, V> keyValueStore, Class<K> keyClass, Class<V> valueClass) {
        this.keyValueStore = keyValueStore;
        this.db = DBMaker
                .fileDB(baseDirectory + "/" + (keyClass.getTypeName() + "-" + valueClass.getTypeName()).toLowerCase())
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();

        cache = db.hashMap(keyClass.getName() + "-" + valueClass.getName())
            .valueSerializer(new Serializer<V>() {
                ObjectMapper objectMapper = new ObjectMapper();
                @Override
                public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull V object) throws IOException {
                    objectMapper.writeValue((DataOutput) dataOutput2, object);
                }
                @Override
                public V deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
                    return objectMapper.readValue((DataInput) dataInput2, valueClass);
                }
            }).keySerializer(new Serializer<K>() {
                ObjectMapper objectMapper = new ObjectMapper();
                @Override
                public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull K alaSpeciesMatchRequest) throws IOException {
                    objectMapper.writeValue((DataOutput) dataOutput2, alaSpeciesMatchRequest);
                }

                @Override
                public K deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
                    return objectMapper.readValue((DataInput) dataInput2, keyClass);
                }
            })
            .createOrOpen();

        this.db.getStore().fileLoad();
    }

    /**
     * Factory method to create instances of KeyValueStore caches.
     *
     * @param keyValueStore store to be cached/wrapped
     * @param keyClass      type descriptor for the key elements
     * @param valueClass    type descriptor for the value elements
     * @param <K1>          type of key elements
     * @param <V1>          type of value elements
     * @return a new instance of KeyValueStore cache
     */
    public static <K1, V1> WarmableCache<K1, V1> cache(String baseDirectory, KeyValueStore<K1, V1> keyValueStore,  Class<K1> keyClass, Class<V1> valueClass) {
        return new MapDBKeyValueStore<>(baseDirectory, keyValueStore, keyClass, valueClass);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        db.commit();
    }

    @Override
    public void putAll(Map<K,V> map) {
        cache.putAll(map);
        db.commit();
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value == null){
            synchronized (key) {
                value = cache.get(key);
                if (value == null) {
                    value = keyValueStore.get(key);
                    cache.put(key, value);
                    db.commit();
                }
            }
        }
        return value;
    }

    @Override
    public void close() throws IOException {
        db.close();
        keyValueStore.close();
    }
}
