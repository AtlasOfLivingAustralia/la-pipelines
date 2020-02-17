package org.gbif.pipelines.kv;

import au.org.ala.kvs.cache.GeocodeMapDBKeyValueStore;
import lombok.SneakyThrows;
import org.gbif.pipelines.parsers.config.model.KvConfig;
import org.gbif.pipelines.parsers.parsers.location.GeocodeService;
import org.gbif.rest.client.configuration.ClientConfiguration;

public class GeocodeServiceFactory {

    private final GeocodeService service;
    private static volatile GeocodeServiceFactory instance;
    private static final Object MUTEX = new Object();

    @SneakyThrows
    private GeocodeServiceFactory(KvConfig config) {
        service = create(config);
    }

    /* TODO Comment */
    public static GeocodeService getInstance(KvConfig config) {
        if (instance == null) {
            synchronized (MUTEX) {
                if (instance == null) {
                    instance = new GeocodeServiceFactory(config);
                }
            }
        }
        return instance.service;
    }

    /* TODO Comment */
    @SneakyThrows
    public static GeocodeService create(KvConfig config) {

        ClientConfiguration clientConfig = ClientConfiguration.builder()
                .withBaseApiUrl(config.getBasePath()) //GBIF base API url
                .withFileCacheMaxSizeMb(config.getCacheSizeMb()) //Max file cache size
                .withTimeOut(config.getTimeout()) //Geocode service connection time-out
                .build();

        GeocodeMapDBKeyValueStore kvStore = GeocodeMapDBKeyValueStore.create(clientConfig);
        return GeocodeService.create(kvStore, BitmapFactory.getInstance(config));
    }
}
