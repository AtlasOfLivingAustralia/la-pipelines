package au.org.ala.kvs.cache;

import au.org.ala.kvs.ALAKvConfig;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.cache.KeyValueCache;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.kv.BitmapFactory;
import org.gbif.pipelines.parsers.config.model.KvConfig;
import org.gbif.pipelines.parsers.parsers.location.GeocodeService;
import org.gbif.rest.client.geocode.GeocodeResponse;

import lombok.SneakyThrows;

/**
 * Factory to get singleton instance of {@link GeocodeService}
 */
public class GeocodeServiceFactory {

  @SneakyThrows
  public static GeocodeService create(KvConfig config, ALAKvConfig alaKvConfig) {
    return GeocodeService.create(createKvStore(alaKvConfig), BitmapFactory.getInstance(config));
  }

  @SneakyThrows
  private static KeyValueStore<LatLng, GeocodeResponse> createKvStore(ALAKvConfig config) {
    if (config == null) {
      return null;
    }
    if (config.isMapDBCacheEnabled()){
      return GeocodeMapDBKeyValueStore.create(config);
    } else {
      GeocodeCache2kKeyValueStore geocodeCache2kKeyValueStore = GeocodeCache2kKeyValueStore.create();
      //return the cache2k version
      return KeyValueCache.cache(geocodeCache2kKeyValueStore, 50000l, LatLng.class, GeocodeResponse.class);
    }
  }
}
