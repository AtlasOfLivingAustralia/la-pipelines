package au.org.ala.kvs.cache;

import au.org.ala.kvs.client.ALANameMatchService;
import au.org.ala.kvs.client.ALANameUsageMatch;
import au.org.ala.kvs.client.ALASpeciesMatchRequest;
import au.org.ala.kvs.client.retrofit.ALANameUsageMatchServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.gbif.api.vocabulary.Rank;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.hbase.Command;
import org.gbif.kvs.species.NameUsageMatchKVStoreFactory;
import org.gbif.kvs.species.SpeciesMatchRequest;
import org.gbif.kvs.species.TaxonParsers;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.gbif.rest.client.species.NameUsageMatch;
import org.gbif.rest.client.species.retrofit.NameMatchServiceSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ALANameMatchKVStoreFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ALANameMatchKVStoreFactory.class);

    public static KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch> alaNameMatchKVStore(ClientConfiguration clientConfiguration) throws IOException {


//        return KeyValueCache.cache(keyValueStore, configuration.getCacheCapacity(), SpeciesMatchRequest.class, NameUsageMatch.class);
//
//
//
//        Cache<SpeciesMatchRequest, ALANameUsageMatch> cache = new org.cache2k.Cache2kBuilder<SpeciesMatchRequest, ALANameUsageMatch>(){}
//                .name("/tmp/ala-name-match-cache")
//                .eternal(true)
//                .build();



//        NameMatchServiceSyncClient nameMatchServiceSyncClient = new NameMatchServiceSyncClient(clientConfiguration);
        Command closeHandler = () -> {
//                nameMatchServiceSyncClient.close();
        };

        ALANameUsageMatchServiceClient wsClient = new ALANameUsageMatchServiceClient(clientConfiguration);

        KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch>  kvs = restKVStore(wsClient, closeHandler);

//        KeyValueStore<SpeciesMatchRequest, NameUsageMatch> keyValueStore = Objects.nonNull(configuration.getHBaseKVStoreConfiguration())?
//                hbaseKVStore(configuration, nameMatchServiceSyncClient, closeHandler) : restKVStore(nameMatchServiceSyncClient, closeHandler);
//        if (Objects.nonNull(configuration.getCacheCapacity())) {
//            return KeyValueCache.cache(keyValueStore, configuration.getCacheCapacity(), SpeciesMatchRequest.class, NameUsageMatch.class);
//        }
//        return keyValueStore;
        return kvs;
    }

    /**
     * Builds a KV Store backed by the rest client.
     */
    private static KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch> restKVStore(ALANameMatchService nameMatchService, Command closeHandler) {
        return new KeyValueStore<ALASpeciesMatchRequest, ALANameUsageMatch>() {

            @Override
            public ALANameUsageMatch get(ALASpeciesMatchRequest key) {
                try {
                    return nameMatchService.match(key);
                } catch (Exception ex) {
                    throw logAndThrow(ex, "Error contacting the species math service");
                }
            }

//            @Override
//            public ALANameUsageMatch get(ALASpeciesMatchRequest key) {
//                try {
//                    ObjectMapper om = new ObjectMapper();
//                    if (key.getScientificName() != null){
//
//
//
//
//                        Map<String, Object> result = om.readValue(new URL("http://localhost:9179/search?q=" + URLEncoder.encode(key.getScientificName(), "UTF-8")), Map.class);
//                        if (result.get("success") != null && result.get("taxonConceptID") != null) {
//                            ALANameUsageMatch atr = ALANameUsageMatch.builder().build();
//                            for (Map.Entry<String, Object> entry : result.entrySet()) {
//                                BeanUtils.setProperty(atr, entry.getKey(), entry.getValue());
//                            }
//                            return atr;
//                        } else {
//                            return ALANameUsageMatch.FAIL;
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                return ALANameUsageMatch.FAIL;
//            }

            @Override
            public void close() throws IOException {
                closeHandler.execute();
            }
        };
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
