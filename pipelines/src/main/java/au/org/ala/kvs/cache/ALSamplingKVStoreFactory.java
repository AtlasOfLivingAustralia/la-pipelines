package au.org.ala.kvs.cache;

import au.org.ala.kvs.client.*;
import au.org.ala.kvs.client.retrofit.ALASamplingServiceClient;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.hbase.Command;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class ALSamplingKVStoreFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ALANameMatchKVStoreFactory.class);

    private static KeyValueStore<ALASamplingRequest, Map<String,String>> mapDBCache = null;


    public static KeyValueStore<ALASamplingRequest, Map<String,String>> alaNameMatchKVStore(ClientConfiguration clientConfiguration) throws IOException {

        ALASamplingServiceClient wsClient = new ALASamplingServiceClient(clientConfiguration);
        Command closeHandler = () -> {
            try {
                wsClient.close();
            } catch (Exception e){
                logAndThrow(e, "Unable to close");
            }
        };
        KeyValueStore<ALASamplingRequest, Map<String,String>>  kvs = mapDBBackedKVStore(wsClient, closeHandler);
        return kvs;
    }

    /**
     * Builds a KV Store backed by the rest client.
     */
    private synchronized static KeyValueStore<ALASamplingRequest, Map<String,String>> mapDBBackedKVStore(ALASamplingServiceClient client, Command closeHandler) {

        if (mapDBCache == null) {
            KeyValueStore kvs = new KeyValueStore<ALASamplingRequest, Map<String,String>>() {
                @Override
                public Map<String,String> get(ALASamplingRequest key) {
                    try {
                        return client.sample(key);
                    } catch (Exception ex) {
                        throw logAndThrow(ex, "Error contacting the species match service");
                    }
                }

                @Override
                public void close() throws IOException {
                    closeHandler.execute();
                }
            };
            mapDBCache = MapDBKeyValueStore.cache(kvs, 100000l, ALASamplingRequest.class, Map.class);
        }

        return mapDBCache;
    }

    /**
     * Wraps an exception into a {@link RuntimeException}.
     * @param throwable to propagate
     * @param message to log and use for the exception wrapper
     * @return a new {@link RuntimeException}
     */
    private static RuntimeException logAndThrow(Throwable throwable, String message) {
        LOG.error(message, throwable);
        return new RuntimeException(throwable);
    }
}
