package au.org.ala.pipelines.transforms;

import java.util.Properties;

import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.parsers.config.factory.KvConfigFactory;
import org.gbif.pipelines.parsers.config.model.KvConfig;
import org.gbif.rest.client.configuration.ClientConfiguration;
import org.gbif.rest.client.geocode.GeocodeResponse;

import au.org.ala.kvs.cache.GeocodeMapDBKeyValueStore2;

public class LocationTransform extends org.gbif.pipelines.transforms.core.LocationTransform {

  private LocationTransform(KeyValueStore<LatLng, GeocodeResponse> kvStore, KvConfig kvConfig) {
    super(kvStore, kvConfig);
  }

  public static LocationTransform create(Properties properties) {
    KvConfig config = KvConfigFactory.create(properties, KvConfigFactory.GEOCODE_PREFIX);
    return new LocationTransform(null, config);
  }

  @Setup
  @Override
  public void setup() {
    KvConfig config = this.getKvConfig();
    if (config != null) {
      ClientConfiguration clientConfig = ClientConfiguration.builder()
          .withBaseApiUrl(config.getBasePath()) //GBIF base API url
          .withFileCacheMaxSizeMb(config.getCacheSizeMb()) //Max file cache size
          .withTimeOut(config.getTimeout()) //Geocode service connection time-out
          .build();

      this.setKvStore(GeocodeMapDBKeyValueStore2.create(clientConfig));
    }
  }

}
