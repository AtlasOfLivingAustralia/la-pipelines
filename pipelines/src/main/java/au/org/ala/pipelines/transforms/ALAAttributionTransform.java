package au.org.ala.pipelines.transforms;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.ALAKvConfigFactory;
import au.org.ala.kvs.cache.ALAAttributionKVStoreFactory;
import au.org.ala.kvs.cache.ALACollectionKVStoreFactory;
import au.org.ala.kvs.cache.ALANameMatchKVStoreFactory;
import au.org.ala.kvs.client.ALACollectionLookup;
import au.org.ala.kvs.client.ALACollectionMatch;
import au.org.ala.kvs.client.ALACollectoryMetadata;
import au.org.ala.pipelines.interpreters.ALAAttributionInterpreter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollectionView;
import org.gbif.kvs.KeyValueStore;
import org.gbif.pipelines.core.Interpretation;
import org.gbif.pipelines.core.interpreters.core.TaxonomyInterpreter;
import org.gbif.pipelines.io.avro.*;
import org.gbif.pipelines.transforms.SerializableConsumer;
import org.gbif.pipelines.transforms.Transform;
import org.gbif.pipelines.transforms.core.LocationTransform;
import org.gbif.rest.client.configuration.ClientConfiguration;

import java.util.Optional;
import java.util.Properties;

import static au.org.ala.pipelines.common.ALARecordTypes.ALA_ATTRIBUTION;

/**
 * ALA attribution transform for adding ALA attribution retrieved from the collectory to interpreted occurrence data.
 *
 * Beam level transformations for the DWC Taxon, reads an avro, writes an avro, maps from value to keyValue and
 * transforms form {@link ExtendedRecord} to {@link TaxonRecord}.
 * <p>
 * ParDo runs sequence of interpretations for {@link TaxonRecord} using {@link ExtendedRecord} as
 * a source and {@link TaxonomyInterpreter} as interpretation steps
 *
 * @see <a href="https://dwc.tdwg.org/terms/#taxon</a>
 */
@Slf4j
public class ALAAttributionTransform extends Transform<ExtendedRecord, ALAAttributionRecord> {

    private final ALAKvConfig kvConfig;

    private KeyValueStore<String, ALACollectoryMetadata> dataResourceKvStore;

    private KeyValueStore<ALACollectionLookup, ALACollectionMatch> collectionKvStore;

    private PCollectionView<MetadataRecord> metadataView;

    private ALAAttributionTransform(KeyValueStore<String, ALACollectoryMetadata> dataResourceKvStore,
                                    ALAKvConfig kvConfig) {
        super(ALAAttributionRecord.class, ALA_ATTRIBUTION, ALAAttributionTransform.class.getName(), "alaAttributionRecordsCount");
        this.dataResourceKvStore = dataResourceKvStore;
        this.kvConfig = kvConfig;
    }

    public static ALAAttributionTransform create(Properties properties) {
        ALAKvConfig config = ALAKvConfigFactory.create(properties);
        return new ALAAttributionTransform(null, config);
    }

    public ALAAttributionTransform counterFn(SerializableConsumer<String> counterFn) {
        setCounterFn(counterFn);
        return this;
    }

    public ALAAttributionTransform init() {
        setup();
        return this;
    }

    public static ALAAttributionTransform create() {
        return new ALAAttributionTransform(null, null);
    }

    @SneakyThrows
    @Setup
    public void setup() {
        if (kvConfig != null) {
            ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                    .withBaseApiUrl(kvConfig.getCollectoryBasePath()) //GBIF base API url
                    .withTimeOut(kvConfig.getTimeout()) //Geocode service connection time-out
                    .build();

            this.dataResourceKvStore = ALAAttributionKVStoreFactory.alaAttributionKVStore(clientConfiguration);
            this.collectionKvStore = ALACollectionKVStoreFactory.alaAttributionKVStore(clientConfiguration);
        }
    }

    public ParDo.SingleOutput<ExtendedRecord, ALAAttributionRecord> interpret(PCollectionView<MetadataRecord> metadataView) {
        this.metadataView = metadataView;
        return ParDo.of(this).withSideInputs(metadataView);
    }

    @Override
    @ProcessElement
    public void processElement(ProcessContext c) {
        processElement(c.element(), c.sideInput(metadataView)).ifPresent(c::output);
    }

    @Override
    public Optional<ALAAttributionRecord> convert(ExtendedRecord extendedRecord) {
        throw new IllegalArgumentException("Method is not implemented!");
    }

    public Optional<ALAAttributionRecord> processElement(ExtendedRecord source, MetadataRecord mdr) {

        ALAAttributionRecord tr = ALAAttributionRecord.newBuilder().setId(source.getId()).build();
        Interpretation.from(source)
                .to(tr)
                .via(ALAAttributionInterpreter.interpretDatasetKey(mdr, dataResourceKvStore))
                .via(ALAAttributionInterpreter.interpretCodes(collectionKvStore))
        ;
        // the id is null when there is an error in the interpretation. In these
        // cases we do not write the taxonRecord because it is totally empty.
        return  Optional.of(tr);
    }
}
