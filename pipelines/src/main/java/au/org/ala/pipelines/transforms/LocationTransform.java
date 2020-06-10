package au.org.ala.pipelines.transforms;

import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.ALAKvConfigFactory;
import au.org.ala.pipelines.interpreters.ALALocationInterpreter;
import org.gbif.pipelines.core.Interpretation;
import org.gbif.pipelines.core.interpreters.core.LocationInterpreter;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.LocationRecord;
import org.gbif.pipelines.io.avro.MetadataRecord;
import org.gbif.pipelines.parsers.config.factory.KvConfigFactory;
import org.gbif.pipelines.parsers.config.model.KvConfig;

import au.org.ala.kvs.cache.GeocodeServiceFactory;

public class LocationTransform extends org.gbif.pipelines.transforms.core.LocationTransform {

  KvConfig kvConfig;
  ALAKvConfig alaKvConfig;

  private LocationTransform(KvConfig kvConfig, ALAKvConfig alaKvConfig) {
    super(kvConfig);
    this.kvConfig = kvConfig;
    this.alaKvConfig = alaKvConfig;
  }

  public static LocationTransform create(Properties properties) {
    ALAKvConfig alaKvConfig = ALAKvConfigFactory.create(properties);
    KvConfig kv = KvConfigFactory.create(properties, KvConfigFactory.GEOCODE_PREFIX);
    KvConfig kvConfig = KvConfig
        .create(alaKvConfig.getGeocodeBasePath(), kv.getTimeout(), kv.getCacheSizeMb(),
            kv.getTableName(), kv.getZookeeperUrl(), kv.getNumOfKeyBuckets(), true,
            kv.getImagePath());
    return new LocationTransform(kvConfig, alaKvConfig);
  }

  /**
   * Initializes resources using singleton factory can be useful in case of non-Beam pipeline
   */
  @Override
  public LocationTransform init() {
    setService(GeocodeServiceFactory.create(kvConfig, alaKvConfig));
    return this;
  }

  @Setup
  @Override
  public void setup() {
    if (kvConfig != null) {
      this.setService(GeocodeServiceFactory.create(kvConfig, alaKvConfig));
    }
  }

  @Override
  public Optional<LocationRecord> processElement(ExtendedRecord source, MetadataRecord mdr) {

    LocationRecord lr = LocationRecord.newBuilder()
        .setId(source.getId())
        .setCreated(Instant.now().toEpochMilli())
        .build();

    Optional<LocationRecord> result = Interpretation.from(source)
        .to(lr)
        .when(er -> !er.getCoreTerms().isEmpty())
        .via(ALALocationInterpreter.interpretCountryAndCoordinates(getService(), mdr))
        .via(ALALocationInterpreter.interpretStateProvince(getService()))
        .via(LocationInterpreter::interpretContinent)
        .via(LocationInterpreter::interpretWaterBody)
        .via(LocationInterpreter::interpretMinimumElevationInMeters)
        .via(LocationInterpreter::interpretMaximumElevationInMeters)
        .via(LocationInterpreter::interpretElevation)
        .via(LocationInterpreter::interpretMinimumDepthInMeters)
        .via(LocationInterpreter::interpretMaximumDepthInMeters)
        .via(LocationInterpreter::interpretDepth)
        .via(LocationInterpreter::interpretMinimumDistanceAboveSurfaceInMeters)
        .via(LocationInterpreter::interpretMaximumDistanceAboveSurfaceInMeters)
        .via(LocationInterpreter::interpretCoordinatePrecision)
        .via(LocationInterpreter::interpretCoordinateUncertaintyInMeters)
        .via(ALALocationInterpreter::interpretGeodetic)
        .via(ALALocationInterpreter::interpretCoordinateUncertainty)
        .get();

    result.ifPresent(r -> this.incCounter());

    return result;
  }
}
