package au.org.ala.kvs.cache;

import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.kv.BitmapFactory;
import org.gbif.pipelines.parsers.config.model.KvConfig;
import org.gbif.pipelines.parsers.parsers.location.GeocodeService;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.gbif.rest.client.geocode.GeocodeResponse;

import lombok.SneakyThrows;

/**
 * Factory to get singleton instance of {@link GeocodeService}
 */
public class GeocodeServiceFactory {

  @SneakyThrows
  public static GeocodeService create(KvConfig config) {
    return GeocodeService.create(creatKvStore(config), BitmapFactory.getInstance(config));
  }

  @SneakyThrows
  private static KeyValueStore<LatLng, GeocodeResponse> creatKvStore(KvConfig config) {
    if (config == null) {
      return null;
    }

    ClientConfiguration clientConfig = ClientConfiguration.builder()
        .withBaseApiUrl(config.getBasePath()) //GBIF base API url
        .withFileCacheMaxSizeMb(config.getCacheSizeMb()) //Max file cache size
        .withTimeOut(config.getTimeout()) //Geocode service connection time-out
        .build();

    return GeocodeMapDBKeyValueStore.create(clientConfig);
  }

}
