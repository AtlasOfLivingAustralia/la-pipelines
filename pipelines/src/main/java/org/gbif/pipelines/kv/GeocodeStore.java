package org.gbif.pipelines.kv;

import au.org.ala.kvs.cache.ALANameMatchKVStoreFactory;
import au.org.ala.kvs.cache.GeocodeMapDBKeyValueStore;
import au.org.ala.kvs.cache.MapDBKeyValueStore;
import au.org.ala.kvs.client.ALANameUsageMatch;
import au.org.ala.kvs.client.ALASpeciesMatchRequest;
import org.apache.hadoop.hbase.util.Bytes;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.cache.KeyValueCache;
import org.gbif.kvs.conf.CachedHBaseKVStoreConfiguration;
import org.gbif.kvs.geocode.GeocodeKVStoreFactory;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.kvs.hbase.Command;
import org.gbif.kvs.hbase.HBaseKVStoreConfiguration;
import org.gbif.kvs.hbase.HBaseStore;
import org.gbif.pipelines.parsers.config.model.KvConfig;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.gbif.rest.client.geocode.GeocodeResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.gbif.rest.client.geocode.GeocodeService;
import org.gbif.rest.client.geocode.Location;
import org.gbif.rest.client.geocode.retrofit.GeocodeServiceSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Create KV store for geocode lookup using KV store {@link KvConfig}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeocodeStore {

    private static final Logger LOG = LoggerFactory.getLogger(ALANameMatchKVStoreFactory.class);

    private static KeyValueStore<LatLng, GeocodeResponse> mapDBCache = null;

    @SneakyThrows
    public static KeyValueStore<LatLng, GeocodeResponse> create(KvConfig config) {
        if (config != null) {
            ClientConfiguration clientConfig = ClientConfiguration.builder()
                    .withBaseApiUrl(config.getBasePath()) //GBIF base API url
                    .withFileCacheMaxSizeMb(config.getCacheSizeMb()) //Max file cache size
                    .withTimeOut(config.getTimeout()) //Geocode service connection time-out
                    .build();

            if (config.getZookeeperUrl() != null && !config.isRestOnly()) {

                CachedHBaseKVStoreConfiguration geocodeKvStoreConfig = CachedHBaseKVStoreConfiguration.builder()
                        .withValueColumnQualifier("j") //stores JSON data
                        .withHBaseKVStoreConfiguration(HBaseKVStoreConfiguration.builder()
                                .withTableName(config.getTableName()) //Geocode KV HBase table
                                .withColumnFamily("v") //Column in which qualifiers are stored
                                .withNumOfKeyBuckets(
                                        config.getNumOfKeyBuckets()) //Buckets for salted key generations == to # of region servers
                                .withHBaseZk(config.getZookeeperUrl()) //HBase Zookeeper ensemble
                                .build())
                        .withCacheCapacity(15_000L)
                        .build();

                return GeocodeKVStoreFactory.simpleGeocodeKVStore(geocodeKvStoreConfig, clientConfig);
            } else {
                GeocodeServiceSyncClient geocodeService =  new GeocodeServiceSyncClient(clientConfig);
                return mapDBGeocodeKVStore(geocodeService, () -> {
                    try {
                        geocodeService.close();
                    } catch (IOException ex) {
                        throw logAndThrow(ex, "Error closing client");
                    }
                });
            }
        }
        return null;
    }

    public static KeyValueStore<LatLng, GeocodeResponse> dummyGeocodeKVStore() {
        KeyValueStore<LatLng, GeocodeResponse> keyValueStore = new KeyValueStore<LatLng, GeocodeResponse>() {
            @Override
            public GeocodeResponse get(LatLng key) {
                GeocodeResponse g = new GeocodeResponse();
                g.setLocations(new ArrayList<Location>());
                return g;
            }

            @Override
            public void close() throws IOException {
            }
        };
        return keyValueStore;
    }


    public static synchronized KeyValueStore<LatLng, GeocodeResponse> mapDBGeocodeKVStore(GeocodeService geocodeService, Command closeHandler) throws IOException {

        if (mapDBCache == null) {
            KeyValueStore<LatLng, GeocodeResponse> kvs = new KeyValueStore<LatLng, GeocodeResponse>() {

                @Override
                public GeocodeResponse get(LatLng key) {
                    try {
                        Collection<Location> locs = geocodeService.reverse(key.getLatitude(), key.getLatitude());
                        GeocodeResponse g = new GeocodeResponse();
                        g.setLocations(locs);
                        return g;
                    } catch (Exception ex) {
                        throw logAndThrow(ex, "Error contacting the species match service");
                    }
                }

                @Override
                public void close() throws IOException {
                    closeHandler.execute();
                }
            };
            mapDBCache = GeocodeMapDBKeyValueStore.cache(kvs);
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
