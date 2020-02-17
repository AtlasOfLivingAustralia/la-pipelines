package au.org.ala.pipelines.transforms;

import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.ALAKvConfigFactory;
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

  private LocationTransform(KvConfig kvConfig) {
    super(kvConfig);
    this.kvConfig  = kvConfig;
  }

  public static LocationTransform create(Properties properties) {
    ALAKvConfig config = ALAKvConfigFactory.create(properties);
    KvConfig kv = KvConfigFactory.create(properties, KvConfigFactory.GEOCODE_PREFIX);
    KvConfig kvConfig = KvConfig.create(config.getGeocodeBasePath(), kv.getTimeout(), kv.getCacheSizeMb(), kv.getTableName(), kv.getZookeeperUrl(), kv.getNumOfKeyBuckets(), true,  kv.getImagePath());
    return new LocationTransform(kvConfig);
  }

  @Override
  /** Initializes resources using singleton factory can be useful in case of non-Beam pipeline */
  public LocationTransform init() {
    setService(GeocodeServiceFactory.create(kvConfig));
    return this;
  }

  @Setup
  @Override
  public void setup() {
    KvConfig config = this.getKvConfig();
    if (config != null) {
      this.setService(GeocodeServiceFactory.create(config));
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
            .via(LocationInterpreter.interpretCountryAndCoordinates(getService(), mdr))
            .via(LocationInterpreter::interpretContinent)
            .via(LocationInterpreter::interpretWaterBody)
            .via(LocationInterpreter::interpretStateProvince)
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
            .get();

    result.ifPresent(r -> this.incCounter());

    return result;
  }


  @Teardown
  @Override
  public void tearDown() {
//    this.service.close();
  }

}
