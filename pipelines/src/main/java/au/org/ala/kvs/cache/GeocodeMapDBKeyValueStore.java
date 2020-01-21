package au.org.ala.kvs.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.kvs.KeyValueStore;
import org.gbif.rest.client.geocode.GeocodeResponse;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.gbif.kvs.geocode.*;

import java.util.concurrent.ConcurrentMap;

/**
 * MapDB implementation of geocode key value store. This type specific version is
 * required due to lack of default constructor on LatLng class.
 */
public class GeocodeMapDBKeyValueStore implements KeyValueStore<LatLng, GeocodeResponse> {

    //Wrapped KeyValueStore
    private final KeyValueStore<LatLng, GeocodeResponse> keyValueStore;

    private final DB db;

    private final ConcurrentMap<LatLng, GeocodeResponse> cache;

    /**
     * Creates a Cache for the KV store.
     *
     * @param keyValueStore wrapped kv store
     */
    private GeocodeMapDBKeyValueStore(KeyValueStore<LatLng, GeocodeResponse> keyValueStore) {
        this.keyValueStore = keyValueStore;
        this.db = DBMaker
                .fileDB("/tmp/" + "geocoderesponse")
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();

        cache = db.hashMap("geocoderesponse")
            .keySerializer(new Serializer<LatLng>() {
                @Override
                public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull LatLng latLng) throws IOException {
                    String toString = latLng.getLatitude() + "&&&" + latLng.getLongitude();
                    dataOutput2.writeChars(toString);
                }
                @Override
                public LatLng deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
                    String[] parts = dataInput2.readLine().split("&&&");
                    return LatLng.builder()
                            .withLatitude(Double.parseDouble(parts[0]))
                            .withLongitude(Double.parseDouble(parts[1]))
                            .build();
                }
            }).valueSerializer(new Serializer<GeocodeResponse>() {
                ObjectMapper objectMapper = new ObjectMapper();
                @Override
                public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull GeocodeResponse resp) throws IOException {
                    objectMapper.writeValue((DataOutput) dataOutput2, resp);
                }

                @Override
                public GeocodeResponse deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
                    return objectMapper.readValue((DataInput) dataInput2, GeocodeResponse.class);
                }
            })
            .createOrOpen();

        this.db.getStore().fileLoad();
    }

    /**
     * Factory method to create instances of KeyValueStore caches.
     *
     * @param keyValueStore store to be cached/wrapped
     * @return a new instance of KeyValueStore cache
     */
    public static GeocodeMapDBKeyValueStore cache(KeyValueStore<LatLng, GeocodeResponse> keyValueStore) {
        return new GeocodeMapDBKeyValueStore(keyValueStore);
    }

    @Override
    public GeocodeResponse get(LatLng key) {
        GeocodeResponse value = cache.get(key);
        if (value == null){
            synchronized (key){
                value = cache.get(key);
                if  (value == null) {
//                    System.out.println("############## CACHE MISS  - " + key.toString());
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
        keyValueStore.close();
    }
}
