package au.org.ala.pipelines.transforms;

import au.org.ala.pipelines.interpreters.ALATaxonomyInterpreter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.gbif.kvs.KeyValueStore;
import org.gbif.kvs.species.SpeciesMatchRequest;
import org.gbif.pipelines.core.Interpretation;
import org.gbif.pipelines.core.interpreters.core.TaxonomyInterpreter;
import org.gbif.pipelines.io.avro.ALATaxonRecord;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.TaxonRecord;
import org.gbif.pipelines.parsers.config.KvConfig;
import org.gbif.pipelines.parsers.config.KvConfigFactory;
import org.gbif.pipelines.transforms.SerializableConsumer;
import org.gbif.pipelines.transforms.Transform;
import org.gbif.rest.client.species.NameUsageMatch;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.Interpretation.RecordType.ALA_TAXONOMY;


/**
 * Beam level transformations for the DWC Taxon, reads an avro, writes an avro, maps from value to keyValue and
 * transforms form {@link ExtendedRecord} to {@link TaxonRecord}.
 * <p>
 * ParDo runs sequence of interpretations for {@link TaxonRecord} using {@link ExtendedRecord} as
 * a source and {@link TaxonomyInterpreter} as interpretation steps
 *
 * @see <a href="https://dwc.tdwg.org/terms/#taxon</a>
 */
@Slf4j
public class ALATaxonomyTransform extends Transform<ExtendedRecord, ALATaxonRecord> {

  private final KvConfig kvConfig;
  private KeyValueStore<SpeciesMatchRequest, NameUsageMatch> kvStore;

  private ALATaxonomyTransform(KeyValueStore<SpeciesMatchRequest, NameUsageMatch> kvStore, KvConfig kvConfig) {
    super(ALATaxonRecord.class, ALA_TAXONOMY, ALATaxonomyTransform.class.getName(), "alaTaxonRecordsCount");
    this.kvStore = kvStore;
    this.kvConfig = kvConfig;
  }

  public static ALATaxonomyTransform create() {
    return new ALATaxonomyTransform(null, null);
  }

  public static ALATaxonomyTransform create(KvConfig kvConfig) {
    return new ALATaxonomyTransform(null, kvConfig);
  }

  public static ALATaxonomyTransform create(KeyValueStore<SpeciesMatchRequest, NameUsageMatch> kvStore) {
    return new ALATaxonomyTransform(kvStore, null);
  }

  public static ALATaxonomyTransform create(String propertiesPath) {
    return new ALATaxonomyTransform(null, KvConfigFactory.create(Paths.get(propertiesPath), KvConfigFactory.TAXONOMY_PREFIX));
  }

  public static ALATaxonomyTransform create(Properties propertiesPath) {
    return new ALATaxonomyTransform(null, KvConfigFactory.create(propertiesPath, KvConfigFactory.TAXONOMY_PREFIX));
  }

  /** Maps {@link ALATaxonRecord} to key value, where key is {@link TaxonRecord#getId} */
  public MapElements<ALATaxonRecord, KV<String, ALATaxonRecord>> toKv() {
    return MapElements.into(new TypeDescriptor<KV<String, ALATaxonRecord>>() {})
        .via((ALATaxonRecord tr) -> KV.of(tr.getId(), tr));
  }

  public ALATaxonomyTransform counterFn(SerializableConsumer<String> counterFn) {
    setCounterFn(counterFn);
    return this;
  }

  public ALATaxonomyTransform init() {
    setup();
    return this;
  }

  @SneakyThrows
  @Setup
  public void setup() {}

  @Teardown
  public void tearDown() {}

  @Override
  public Optional<ALATaxonRecord> convert(ExtendedRecord source) {

    ALATaxonRecord tr = ALATaxonRecord.newBuilder().setId(source.getId()).build();
    Interpretation.from(source)
        .to(tr)
        .when(er -> !er.getCoreTerms().isEmpty())
        .via(ALATaxonomyInterpreter.alaTaxonomyInterpreter(kvStore));

    // the id is null when there is an error in the interpretation. In these
    // cases we do not write the taxonRecord because it is totally empty.
    return  Optional.of(tr);
  }
}
