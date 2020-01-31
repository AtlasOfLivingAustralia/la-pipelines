package au.org.ala.kvs.cache;

import au.org.ala.kvs.client.ALANameMatchService;
import au.org.ala.kvs.client.ALANameUsageMatch;
import au.org.ala.kvs.client.ALASpeciesMatchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.hbase.Command;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * MapDB implementation of key value store.
 */
public class MapDBKeyValueStoreFactory {

//    /**
//     * Builds a KV Store backed by the rest client.
//     */
//    public static KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch> mapDBBackedKVStore(Closeable service) {
//
//        KeyValueStore kvs = new KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch>() {
//            @Override
//            public ALANameUsageMatch get(ALASpeciesMatchRequest key) {
//                try {
//                    return nameMatchService.match(key);
//                } catch (Exception ex) {
//                    throw logAndThrow(ex, "Error contacting the species match service");
//                }
//            }
//
//            @Override
//            public void close() throws IOException {
//                service.close();
//            }
//        };
//        mapDBCache = MapDBKeyValueStore.cache(kvs, 100000l, ALASpeciesMatchRequest.class, ALANameUsageMatch.class);
//    }
//
//    return mapDBCache;

}
