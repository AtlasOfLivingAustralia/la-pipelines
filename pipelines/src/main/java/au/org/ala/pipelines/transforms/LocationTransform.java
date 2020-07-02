package au.org.ala.pipelines.transforms;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.geocode.LatLng;
import au.org.ala.pipelines.interpreters.ALALocationInterpreter;
import org.gbif.pipelines.core.Interpretation;
import org.gbif.pipelines.core.interpreters.core.LocationInterpreter;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.LocationRecord;
import org.gbif.pipelines.io.avro.MetadataRecord;
import org.gbif.pipelines.transforms.SerializableSupplier;
import org.gbif.pipelines.transforms.Transform;
import org.gbif.rest.client.geocode.GeocodeResponse;

import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.ParDo.SingleOutput;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TypeDescriptor;

import lombok.Builder;
import lombok.Setter;

import static org.gbif.pipelines.common.PipelinesVariables.Metrics.LOCATION_RECORDS_COUNT;
import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.Interpretation.RecordType.LOCATION;

@Slf4j
public class LocationTransform extends Transform<ExtendedRecord, LocationRecord> {

  private SerializableSupplier<KeyValueStore<LatLng, GeocodeResponse>> geocodeKvStoreSupplier;
  private KeyValueStore<LatLng, GeocodeResponse> geocodeKvStore;

  @Setter
  private PCollectionView<MetadataRecord> metadataView;

  @Builder(buildMethodName = "create")
  private LocationTransform(
          SerializableSupplier<KeyValueStore<LatLng, GeocodeResponse>> geocodeKvStoreSupplier,
          KeyValueStore<LatLng, GeocodeResponse> geocodeKvStore,
          PCollectionView<MetadataRecord> metadataView) {
    super(LocationRecord.class, LOCATION, org.gbif.pipelines.transforms.core.LocationTransform.class.getName(), LOCATION_RECORDS_COUNT);
    this.geocodeKvStoreSupplier = geocodeKvStoreSupplier;
    this.geocodeKvStore = geocodeKvStore;
    this.metadataView = metadataView;
  }

  /** Maps {@link LocationRecord} to key value, where key is {@link LocationRecord#getId} */
  public MapElements<LocationRecord, KV<String, LocationRecord>> toKv() {
    return MapElements.into(new TypeDescriptor<KV<String, LocationRecord>>() {})
            .via((LocationRecord lr) -> KV.of(lr.getId(), lr));
  }

  @Override
  public SingleOutput<ExtendedRecord, LocationRecord> interpret() {
    return ParDo.of(this).withSideInputs(metadataView);
  }

  /** Beam @Setup initializes resources */
  @Setup
  public void setup() {
    if (geocodeKvStore == null && geocodeKvStoreSupplier != null) {
      log.info("Initialize geocodeKvStore");
      geocodeKvStore = geocodeKvStoreSupplier.get();
    }
  }

  /** Beam @Teardown closes initialized resources */
  @Teardown
  public void tearDown() {
    try {
      if(geocodeKvStore != null) {
        log.info("Close geocodeKvStore");
        geocodeKvStore.close();
      }
    } catch (IOException ex) {
      log.warn("Can't close geocodeKvStore - {}", ex.getMessage());
    }
  }

  @Override
  public Optional<LocationRecord> convert(ExtendedRecord source) {
    throw new IllegalArgumentException("Method is not implemented!");
  }

  @Override
  @ProcessElement
  public void processElement(ProcessContext c) {
    processElement(c.element(), c.sideInput(metadataView)).ifPresent(c::output);
  }

  public Optional<LocationRecord> processElement(ExtendedRecord source, MetadataRecord mdr) {

    LocationRecord lr = LocationRecord.newBuilder()
            .setId(source.getId())
            .setCreated(Instant.now().toEpochMilli())
            .build();

    Optional<LocationRecord> result = Interpretation.from(source)
            .to(lr)
            .when(er -> !er.getCoreTerms().isEmpty())
            .via(LocationInterpreter.interpretCountryAndCoordinates(geocodeKvStore, mdr))
            .via(ALALocationInterpreter.interpretStateProvince(geocodeKvStore))
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
            .via(ALALocationInterpreter::interpretGeoreferenceTerms)
            .via(ALALocationInterpreter::interpretCoordinateUncertainty)
            .get();

    result.ifPresent(r -> this.incCounter());

    return result;
  }
}
