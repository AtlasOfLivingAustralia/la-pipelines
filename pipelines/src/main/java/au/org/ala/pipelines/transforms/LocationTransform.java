package au.org.ala.pipelines.transforms;

import java.util.Properties;

import org.gbif.pipelines.parsers.config.factory.KvConfigFactory;
import org.gbif.pipelines.parsers.config.model.KvConfig;

import au.org.ala.kvs.cache.GeocodeServiceFactory;

public class LocationTransform extends org.gbif.pipelines.transforms.core.LocationTransform {

  private LocationTransform(KvConfig kvConfig) {
    super(kvConfig);
  }

  public static LocationTransform create(Properties properties) {
    KvConfig config = KvConfigFactory.create(properties, "geocode");
    return new LocationTransform(config);
  }

  @Setup
  @Override
  public void setup() {
    KvConfig config = this.getKvConfig();
    if (config != null) {
      this.setService(GeocodeServiceFactory.create(config));
    }
  }

}
