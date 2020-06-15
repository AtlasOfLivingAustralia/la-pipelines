package au.org.ala.kvs.cache;

import au.org.ala.kvs.ALAKvConfig;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.cache.KeyValueCache;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.kv.BitmapFactory;
import org.gbif.pipelines.parsers.config.model.KvConfig;
import org.gbif.pipelines.parsers.parsers.location.GeocodeKvStore;
import org.gbif.rest.client.geocode.GeocodeResponse;

import lombok.SneakyThrows;

/**
 * Factory to get singleton instance of {@link GeocodeKvStore}
 */
public class GeocodeServiceFactory {

  public static GeocodeKvStore create(KvConfig config) {
    return GeocodeKvStore.create(createKvStore(), BitmapFactory.getInstance(config));
  }

  private static KeyValueStore<LatLng, GeocodeResponse> createKvStore() {
    return GeocodeCache2kKeyValueStore.create();
  }
}
