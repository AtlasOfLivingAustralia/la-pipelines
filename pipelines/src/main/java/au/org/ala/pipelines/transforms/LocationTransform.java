package au.org.ala.pipelines.transforms;

import java.util.Properties;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.ALAKvConfigFactory;
import org.gbif.pipelines.parsers.config.factory.KvConfigFactory;
import org.gbif.pipelines.parsers.config.model.KvConfig;

import au.org.ala.kvs.cache.GeocodeServiceFactory;

public class LocationTransform extends org.gbif.pipelines.transforms.core.LocationTransform {

  private LocationTransform(KvConfig kvConfig) {
    super(kvConfig);
  }

  public static LocationTransform create(Properties properties) {
    ALAKvConfig config = ALAKvConfigFactory.create(properties);
    KvConfig kv = KvConfigFactory.create(properties, "geocode");
    KvConfig kvConfig = KvConfig.create(config.getGeocodeBasePath(), kv.getTimeout(), kv.getCacheSizeMb(), kv.getTableName(), kv.getZookeeperUrl(), kv.getNumOfKeyBuckets(), true,  kv.getImagePath());
    return new LocationTransform(kvConfig);
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
