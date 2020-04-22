package au.org.ala.pipelines.beam;

import au.org.ala.kvs.ALAKvConfig;
import au.org.ala.kvs.ALAKvConfigFactory;
import au.org.ala.kvs.cache.ALAAttributionKVStoreFactory;
import au.org.ala.kvs.client.ALACollectoryMetadata;
import au.org.ala.pipelines.common.ALARecordTypes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.CodecFactory;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.kvs.KeyValueStore;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.utils.FsUtils;
import org.gbif.pipelines.io.avro.*;
import org.gbif.pipelines.parsers.utils.ModelUtils;
import org.gbif.rest.client.configuration.ClientConfiguration;

import java.io.File;
import java.util.*;

/**
 * Pipeline responsible for minting UUIDs on records. This works by:
 *
 * 1. Creating a map from ExtendedRecord of UniqueKey -> ExtendedRecord.getId(). The UniqueKey
 * is constructed using the unique terms specified in the collectory.
 *
 * 2. Creating a map from source of UniqueKey -> UUID from previous ingestions (this will be blank for newly imported datasets)
 *
 * 3. Joining the two maps
 *
 * 4. Writing out ALAUUIRecords containing ExtendedRecord.getId() -> UUID. These records are then used in SOLR index generation.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ALAUUIDMintingPipeline {

    private static final CodecFactory BASE_CODEC = CodecFactory.snappyCodec();

    public static final String NO_ID_MARKER = "NO_ID";

    public static void main(String[] args) throws Exception {
        InterpretationPipelineOptions options = PipelinesOptionsFactory.createInterpretation(args);
        run(options);
    }

    public static void run(InterpretationPipelineOptions options) throws Exception {

        Pipeline p = Pipeline.create(options);
        Properties properties = FsUtils.readPropertiesFile(options.getHdfsSiteConfig(), options.getProperties());

        //build the directory path for existing identifiers
        String alaRecordDirectoryPath = options.getTargetPath() + "/" + options.getDatasetId().trim() + "/1/identifiers/" + ALARecordTypes.ALA_UUID.name().toLowerCase();

        //create client configuration
        ALAKvConfig kvConfig = ALAKvConfigFactory.create(properties);
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .withBaseApiUrl(kvConfig.getCollectoryBasePath()) //GBIF base API url
                .withTimeOut(kvConfig.getTimeout()) //Geocode service connection time-out
                .build();

        //create key value store for data resource metadata
        KeyValueStore<String, ALACollectoryMetadata> dataResourceKvStore = ALAAttributionKVStoreFactory.alaAttributionKVStore(clientConfiguration, kvConfig);

        //lookup collectory metadata for this data resource
        ALACollectoryMetadata collectoryMetadata = dataResourceKvStore.get(options.getDatasetId());
        if (collectoryMetadata.equals(ALACollectoryMetadata.EMPTY)){
            throw new RuntimeException("Unable to retrieve dataset metadata for dataset: " + options.getDatasetId());
        }

        //construct unique list of darwin core terms
        List<String> uniqueTerms = collectoryMetadata.getConnectionParameters().getTermsForUniqueKey();
        List<DwcTerm> uniqueDwcTerms = new ArrayList<DwcTerm>();
        for (String uniqueTerm : uniqueTerms){
            Optional<DwcTerm> dwcTerm = getDwcTerm(uniqueTerm);
            if(!dwcTerm.isPresent()){
                throw new RuntimeException("Unrecognised unique term configured for datasource " + options.getDatasetId() + ", term: " + uniqueTerm);
            } else {
                uniqueDwcTerms.add(dwcTerm.get());
            }
        }

        //initialise UniqueKey ->  ExtendedRecord.getId() function
        ExtendedRecordKVFcn extendedRecordKVFcn = new ExtendedRecordKVFcn();
        extendedRecordKVFcn.setup(uniqueDwcTerms);

        log.info("Transform 1: ExtendedRecord er ->  <uniqueKey, er.getId()> - this generates the UniqueKey.....");
        PCollection<KV<String, String>> extendedRecords =
                p.apply(AvroIO.read(ExtendedRecord.class).from("/data/pipelines-data/" + options.getDatasetId().trim() + "/1/interpreted/verbatim/*.avro"))
                .apply(ParDo.of(extendedRecordKVFcn));

        PCollection<KV<String, String>> alaUuids = null;

        log.info("Transform 2: ALAUUIDRecord ur ->  <uniqueKey, uuid>  (assume incomplete)");
        File alaRecordDirectory = new File(alaRecordDirectoryPath);

        if (alaRecordDirectory.exists() && alaRecordDirectory.isDirectory()){

            alaUuids = p.apply(AvroIO.read(ALAUUIDRecord.class)
                            .from(alaRecordDirectory + "/*.avro"))
                            .apply(ParDo.of(new ALAUUIDRecordKVFcn()));
        } else {

            TypeDescriptor<KV<String,String>> td = new TypeDescriptor<KV<String,String>>() {};
            log.warn("[WARNING] Previous ALAUUIDRecord records where not found. This is expected for new datasets, but is a problem" +
                    "for previously loaded datasets - will mint new ones......");
            alaUuids = p.apply(Create.empty(td));
        }

        log.info("Create join collection");
        PCollection<KV<String, KV<String, String>>> joinedPcollection =
                org.apache.beam.sdk.extensions.joinlibrary.Join.leftOuterJoin(extendedRecords, alaUuids, NO_ID_MARKER);

        log.info("Create ALAUUIDRecords and write out to AVRO");
        joinedPcollection
                .apply(ParDo.of(new CreateALAUUIDRecordFcn()))
                .apply(AvroIO.write(ALAUUIDRecord.class)
                        .to(alaRecordDirectory + "_new/interpret")
                        .withSuffix(".avro")
                        .withCodec(BASE_CODEC));

        log.info("Running the pipeline");
        PipelineResult result = p.run();
        result.waitUntilFinish();

        //TODO duplicate check of ALAUUIDRecord entries.....

        //rename existing
        if (new File(alaRecordDirectoryPath).exists()){
            FileUtils.rename(new File(alaRecordDirectoryPath), new File(alaRecordDirectory + "_backup_" + System.currentTimeMillis()));
        }

        //rename existing
        FileUtils.rename(new File(alaRecordDirectory + "_new"), new File(alaRecordDirectoryPath));
    }

    /**
     * Function to create ALAUUIDRecords.
     */
    static class CreateALAUUIDRecordFcn extends DoFn<KV<String, KV<String, String>>, ALAUUIDRecord> {

        @ProcessElement
        public void processElement(@Element KV<String, KV<String, String>> uniqueKeyMap, OutputReceiver<ALAUUIDRecord> out) {
            String uniqueKey = uniqueKeyMap.getKey();
            String id = uniqueKeyMap.getValue().getKey();
            String uuid = uniqueKeyMap.getValue().getValue();
            if (uuid.equals(NO_ID_MARKER)){
                uuid = UUID.randomUUID().toString();
            }
            ALAUUIDRecord aur = ALAUUIDRecord.newBuilder().setUniqueKey(uniqueKey).setUuid(uuid).setId(id).build();
            out.output(aur);
        }
    }

    /**
     * Transform 1: ExtendedRecord er ->  <uniqueKey, er.getId()> - this generates the composite unique key from the
     * specified darwin core terms in the collectory.
     *
     * This will construct the unique keys from source data and pass back as a Map(uniqueKey -> ID)
     * where ID is the ExtendedRecord.getId() which only unique within the scope of this
     * dataset.
     */
    static class ExtendedRecordKVFcn extends DoFn<ExtendedRecord, KV<String, String>> {

        public static List<DwcTerm> dwcTermList;

        @ProcessElement
        public void processElement(@Element ExtendedRecord source, OutputReceiver<KV<String, String>> out, ProcessContext c) {

            List<String> uniqueValues = new ArrayList<String>();
            for (DwcTerm dwcTerm : dwcTermList){
                String value = ModelUtils.extractNullAwareValue(source, dwcTerm);
                if (value == null || StringUtils.trimToNull(value) == null){
                    throw new RuntimeException("Unable to load dataset. Unique term empty for record term: " + dwcTerm);
                }
                uniqueValues.add(value.trim());
            }
            //create the unique key
            String uniqueKey = String.join("|", uniqueValues);
            out.output(KV.of(uniqueKey, source.getId()));
        }

        public void setup(List<DwcTerm> dwcTermList){
            this.dwcTermList = dwcTermList;
        }
    }

    /**
     * Transform to create a map of unique keys built from previous runs and UUID.
     */
    static class ALAUUIDRecordKVFcn extends DoFn<ALAUUIDRecord, KV<String, String>> {
        @ProcessElement
        public void processElement(@Element ALAUUIDRecord alaUUIDRecord, OutputReceiver<KV<String, String>> out) {
            out.output(KV.of(alaUUIDRecord.getUniqueKey(), alaUUIDRecord.getUuid()));
        }
    }

    static Optional<DwcTerm> getDwcTerm(String name){
        try {
            return Optional.of(DwcTerm.valueOf(name));
        } catch (IllegalArgumentException e){
            return Optional.empty();
        }
    }
}
