package au.org.ala.kvs.cache;

import au.org.ala.kvs.client.ALANameMatchService;
import au.org.ala.kvs.client.ALANameUsageMatch;
import au.org.ala.kvs.client.ALASpeciesMatchRequest;
import au.org.ala.kvs.client.retrofit.ALANameUsageMatchServiceClient;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.cache.KeyValueCache;
import org.gbif.kvs.hbase.Command;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class ALANameMatchKVStoreFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ALANameMatchKVStoreFactory.class);

    public static KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch> alaNameMatchKVStore(ClientConfiguration clientConfiguration) throws IOException {

        ALANameUsageMatchServiceClient wsClient = new ALANameUsageMatchServiceClient(clientConfiguration);
        Command closeHandler = () -> {
                try {
                    wsClient.close();
                } catch (Exception e){
                    logAndThrow(e, "Unable to close");
                }
        };
        KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch>  kvs = restKVStore(wsClient, closeHandler);
        return kvs;
    }

    /**
     * Builds a KV Store backed by the rest client.
     */
    private static KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch> restKVStore(ALANameMatchService nameMatchService, Command closeHandler) {
        KeyValueStore kvs = new KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch>() {
            @Override
            public ALANameUsageMatch get(ALASpeciesMatchRequest key) {
                try {
                    return nameMatchService.match(key);
                } catch (Exception ex) {
                    throw logAndThrow(ex, "Error contacting the species match service");
                }
            }
            @Override
            public void close() throws IOException {
                closeHandler.execute();
            }
        };
        return KeyValueCache.cache(kvs, 1000l, ALASpeciesMatchRequest.class, ALANameUsageMatch.class);
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
