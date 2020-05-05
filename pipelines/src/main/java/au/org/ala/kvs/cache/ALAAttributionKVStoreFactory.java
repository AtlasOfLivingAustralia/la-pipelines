package au.org.ala.kvs.cache;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.client.*;
import au.org.ala.kvs.client.retrofit.ALACollectoryServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.hbase.Command;
import org.gbif.rest.client.configuration.ClientConfiguration;

import java.io.IOException;

/**
 * Key value store factory for Attribution
 */
@Slf4j
public class ALAAttributionKVStoreFactory {

    private static KeyValueStore<String, ALACollectoryMetadata> mapDBCache = null;

    /**
     * Retrieve KV Store for Collectory Metadata.
     *
     * @param clientConfiguration
     * @return
     * @throws IOException
     */
    public static KeyValueStore<String, ALACollectoryMetadata> alaAttributionKVStore(ClientConfiguration clientConfiguration, ALAKvConfig kvConfig) throws IOException {

        ALACollectoryServiceClient wsClient = new ALACollectoryServiceClient(clientConfiguration);
        Command closeHandler = () -> {
                try {
                    wsClient.close();
                } catch (Exception e){
                    logAndThrow(e, "Unable to close");
                }
        };
        KeyValueStore<String, ALACollectoryMetadata>  kvs = mapDBBackedKVStore(wsClient, closeHandler, kvConfig);
        return kvs;
    }

    /**
     * Builds a KV Store for Collectory Metadata backed by a MapDB database.
     */
    private synchronized static KeyValueStore<String, ALACollectoryMetadata> mapDBBackedKVStore(ALACollectoryService service, Command closeHandler, ALAKvConfig kvConfig) {

        if (mapDBCache == null) {
            KeyValueStore kvs = new KeyValueStore<String, ALACollectoryMetadata>() {
                @Override
                public ALACollectoryMetadata get(String key) {
                    try {
                        return service.lookupDataResource(key);
                    } catch (Exception ex) {
                        log.error("Error contacting the collectory service to retrieve data resource metadata. Has resource been removed ? " + key, ex);
                        return ALACollectoryMetadata.EMPTY;
                    }
                }

                @Override
                public void close() throws IOException {
                    closeHandler.execute();
                }
            };
            mapDBCache = MapDBKeyValueStore.cache(kvConfig.getCacheDirectoryPath(), kvs, String.class, ALACollectoryMetadata.class);
        }

        return mapDBCache;
    }

    /**
     * Wraps an exception into a {@link RuntimeException}.
     *
     * @param throwable to propagate
     * @param message to log and use for the exception wrapper
     * @return a new {@link RuntimeException}
     */
    private static RuntimeException logAndThrow(Throwable throwable, String message) {
        log.error(message, throwable);
        return new RuntimeException(throwable);
    }
}
