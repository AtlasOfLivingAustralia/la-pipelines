package au.org.ala.pipelines.transforms;


import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.ALAKvConfigFactory;
import au.org.ala.kvs.cache.ALSamplingKVStoreFactory;
import au.org.ala.kvs.client.ALASamplingRequest;
import au.org.ala.pipelines.interpreters.ALASamplingInterpreter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.geocode.LatLng;
import org.gbif.pipelines.core.Interpretation;
import org.gbif.pipelines.core.interpreters.specific.AustraliaSpatialInterpreter;
import org.gbif.pipelines.io.avro.AustraliaSpatialRecord;
import org.gbif.pipelines.io.avro.LocationRecord;
import org.gbif.pipelines.transforms.SerializableConsumer;
import org.gbif.pipelines.transforms.Transform;
import org.gbif.pipelines.transforms.specific.AustraliaSpatialTransform;
import org.gbif.rest.client.configuration.ClientConfiguration;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static org.gbif.pipelines.common.PipelinesVariables.Metrics.AUSTRALIA_SPATIAL_RECORDS_COUNT;
import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.Interpretation.RecordType.AUSTRALIA_SPATIAL;

/**
 * Beam level transformations for the Australia location, reads an avro, writes an avro, maps from value to keyValue
 * and transforms form {@link LocationRecord} to {@link AustraliaSpatialRecord}.
 * <p>
 * ParDo runs sequence of interpretations for {@link AustraliaSpatialRecord} using {@link LocationRecord} as
 * a source and {@link AustraliaSpatialInterpreter} as interpretation steps
 */
@Slf4j
public class ALASamplingTransform extends Transform<LocationRecord, AustraliaSpatialRecord> {

    private final ALAKvConfig kvConfig;
    private KeyValueStore<ALASamplingRequest, Map<String,String>> kvStore;

    private ALASamplingTransform(KeyValueStore<ALASamplingRequest, Map<String,String>> kvStore, ALAKvConfig kvConfig) {
        super(AustraliaSpatialRecord.class, AUSTRALIA_SPATIAL, AustraliaSpatialTransform.class.getName(), AUSTRALIA_SPATIAL_RECORDS_COUNT);
        this.kvStore = kvStore;
        this.kvConfig = kvConfig;
    }

    public static ALASamplingTransform create() {
        return new ALASamplingTransform(null, null);
    }

    public static ALASamplingTransform create(KeyValueStore<ALASamplingRequest, Map<String,String>> kvStore) {
        return new ALASamplingTransform(kvStore, null);
    }

    public static ALASamplingTransform create(Properties properties) {
        ALAKvConfig config = ALAKvConfigFactory.create(properties);
        return new ALASamplingTransform(null, config);
    }

    /** Maps {@link AustraliaSpatialRecord} to key value, where key is {@link AustraliaSpatialRecord#getId} */
    public MapElements<AustraliaSpatialRecord, KV<String, AustraliaSpatialRecord>> toKv() {
        return MapElements.into(new TypeDescriptor<KV<String, AustraliaSpatialRecord>>() {})
                .via((AustraliaSpatialRecord ar) -> KV.of(ar.getId(), ar));
    }

    public ALASamplingTransform counterFn(SerializableConsumer<String> counterFn) {
        setCounterFn(counterFn);
        return this;
    }

    public ALASamplingTransform init() {
        setup();
        return this;
    }

    @SneakyThrows
    @Setup
    public void setup() {
        if (kvConfig != null) {
            ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                    .withBaseApiUrl(kvConfig.getSpatialBasePath()) //GBIF base API url
                    .withTimeOut(kvConfig.getTimeout()) //Geocode service connection time-out
                    .build();

            kvStore = ALSamplingKVStoreFactory.alaNameMatchKVStore(clientConfiguration);
        }
    }

    @Teardown
    public void tearDown() {
        if (Objects.nonNull(kvStore)) {
            try {
                kvStore.close();
            } catch (IOException ex) {
                log.error("Error closing KVStore", ex);
            }
        }
    }

    @Override
    public Optional<AustraliaSpatialRecord> convert(LocationRecord source) {
        return Interpretation.from(source)
                .to(lr -> AustraliaSpatialRecord.newBuilder()
                        .setId(lr.getId())
                        .setCreated(Instant.now().toEpochMilli())
                        .build())
                .when(lr -> Optional.ofNullable(lr.getCountryCode())
                        .filter(c -> new LatLng(lr.getDecimalLatitude(), lr.getDecimalLongitude()).isValid())
                        .isPresent())
                .via(ALASamplingInterpreter.interpret(kvStore))
                .get();
    }
}