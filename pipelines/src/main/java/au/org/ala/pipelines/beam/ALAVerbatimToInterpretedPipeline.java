package au.org.ala.pipelines.beam;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.gbif.api.model.pipelines.StepType;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.options.PipelinesOptionsFactory;
import org.gbif.pipelines.ingest.utils.FsUtils;
import org.gbif.pipelines.ingest.utils.MetricsHandler;
import org.gbif.pipelines.io.avro.BasicRecord;
import org.gbif.pipelines.io.avro.ExtendedRecord;
import org.gbif.pipelines.io.avro.MetadataRecord;
import org.gbif.pipelines.transforms.common.UniqueIdTransform;
import org.gbif.pipelines.transforms.converters.OccurrenceExtensionTransform;
import org.gbif.pipelines.transforms.core.*;
import org.gbif.pipelines.transforms.extension.AudubonTransform;
import org.gbif.pipelines.transforms.extension.ImageTransform;
import org.gbif.pipelines.transforms.extension.MeasurementOrFactTransform;
import org.gbif.pipelines.transforms.extension.MultimediaTransform;
import org.gbif.pipelines.transforms.metadata.MetadataTransform;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.slf4j.MDC;

import au.org.ala.pipelines.transforms.ALAAttributionTransform;
import au.org.ala.pipelines.transforms.ALATaxonomyTransform;
import au.org.ala.pipelines.transforms.LocationTransform;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Pipeline sequence: <p>
 * <pre>
 *    1) Reads verbatim.avro file
 *    2) Interprets and converts avro {@link ExtendedRecord} file to:
 *      {@link MetadataRecord},
 *      {@link BasicRecord},
 *      {@link org.gbif.pipelines.io.avro.TemporalRecord},
 *      {@link org.gbif.pipelines.io.avro.MultimediaRecord},
 *      {@link org.gbif.pipelines.io.avro.ImageRecord},
 *      {@link org.gbif.pipelines.io.avro.AudubonRecord},
 *      {@link org.gbif.pipelines.io.avro.MeasurementOrFactRecord},
 *      {@link org.gbif.pipelines.io.avro.TaxonRecord},
 *      {@link org.gbif.pipelines.io.avro.LocationRecord}
 *    3) Writes data to independent files
 * </pre>
 * <p> <p>How to run: <p>
 * <pre>{@code
 * java -jar target/ingest-gbif-standalone-BUILD_VERSION-shaded.jar some.properties
 *
 * or pass all parameters:
 *
 * java -jar target/ingest-gbif-standalone-BUILD_VERSION-shaded.jar
 * --pipelineStep=VERBATIM_TO_ALA_INTERPRETED \
 * --properties=/some/path/to/output/ws.properties
 * --datasetId=0057a720-17c9-4658-971e-9578f3577cf5
 * --attempt=1
 * --interpretationTypes=ALL
 * --runner=SparkRunner
 * --targetPath=/some/path/to/output/
 * --inputPath=/some/path/to/output/0057a720-17c9-4658-971e-9578f3577cf5/1/verbatim.avro
 *
 * }</pre>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ALAVerbatimToInterpretedPipeline {

  public static void main(String[] args) {
    InterpretationPipelineOptions options = PipelinesOptionsFactory.createInterpretation(args);
    run(options);
  }

  public static void run(InterpretationPipelineOptions options) {

    String datasetId = options.getDatasetId();
    Integer attempt = options.getAttempt();
    boolean tripletValid = options.isTripletValid();
    boolean occurrenceIdValid = options.isOccurrenceIdValid();
    boolean useExtendedRecordId = options.isUseExtendedRecordId();
    boolean skipRegistryCalls = options.isSkipRegisrtyCalls();
    String endPointType = options.getEndPointType();

    Set<String> types = options.getInterpretationTypes();

    String targetPath = options.getTargetPath();
    String hdfsSiteConfig = options.getHdfsSiteConfig();
    Properties properties = FsUtils
        .readPropertiesFile(options.getHdfsSiteConfig(), options.getProperties());

    FsUtils.deleteInterpretIfExist(hdfsSiteConfig, targetPath, datasetId, attempt, types);

    MDC.put("datasetId", datasetId);
    MDC.put("attempt", attempt.toString());
    MDC.put("step", StepType.VERBATIM_TO_INTERPRETED.name());

    String id = Long.toString(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

    UnaryOperator<String> pathFn = t -> FsUtils.buildPathInterpretUsingTargetPath(options, t, id);

    log.info("Creating a pipeline from options");

    Pipeline p = Pipeline.create(options);

    // Core
    MetadataTransform metadataTransform = MetadataTransform
        .create(properties, endPointType, attempt, skipRegistryCalls);
    BasicTransform basicTransform = BasicTransform
        .create(properties, datasetId, tripletValid, occurrenceIdValid, useExtendedRecordId);
    VerbatimTransform verbatimTransform = VerbatimTransform.create();
    TemporalTransform temporalTransform = TemporalTransform.create();
//    TaxonomyTransform taxonomyTransform = TaxonomyTransform.create(properties);

    // Extension
    MeasurementOrFactTransform measurementOrFactTransform = MeasurementOrFactTransform.create();
    MultimediaTransform multimediaTransform = MultimediaTransform.create();
    AudubonTransform audubonTransform = AudubonTransform.create();
    ImageTransform imageTransform = ImageTransform.create();

    // ALA specific transforms
    ALAAttributionTransform alaAttributionTransform = ALAAttributionTransform.create(properties);
    ALATaxonomyTransform alaTaxonomyTransform = ALATaxonomyTransform.create(properties);
    LocationTransform locationTransform = LocationTransform.create(properties);

    log.info("Creating beam pipeline");
    // Create and write metadata
    PCollection<MetadataRecord> metadataRecord =
        p.apply("Create metadata collection", Create.of(options.getDatasetId()))
            .apply("Interpret metadata", metadataTransform.interpret());

    metadataRecord.apply("Write metadata to avro", metadataTransform.write(pathFn));

    // Create View for the further usage
    PCollectionView<MetadataRecord> metadataView =
        metadataRecord
            .apply("Check verbatim transform condition", metadataTransform.checkMetadata(types))
            .apply("Convert into view", View.asSingleton());

    // Interpret and write all record types
    PCollection<ExtendedRecord> uniqueRecords = metadataTransform.metadataOnly(types) ?
        verbatimTransform.emptyCollection(p) :
        p.apply("Read ExtendedRecords", verbatimTransform.read(options.getInputPath()))
            .apply("Read occurrences from extension", OccurrenceExtensionTransform.create())
            .apply("Filter duplicates", UniqueIdTransform.create());

    uniqueRecords
        .apply("Check verbatim transform condition", verbatimTransform.check(types))
        .apply("Write verbatim to avro", verbatimTransform.write(pathFn));

/*    uniqueRecords
        .apply("Check basic transform condition", basicTransform.check(types))
        .apply("Interpret basic", basicTransform.interpret())
        .apply("Write basic to avro", basicTransform.write(pathFn));*/

    uniqueRecords
        .apply("Check temporal transform condition", temporalTransform.check(types))
        .apply("Interpret temporal", temporalTransform.interpret())
        .apply("Write temporal to avro", temporalTransform.write(pathFn));

    uniqueRecords
        .apply("Check multimedia transform condition", multimediaTransform.check(types))
        .apply("Interpret multimedia", multimediaTransform.interpret())
        .apply("Write multimedia to avro", multimediaTransform.write(pathFn));

    uniqueRecords
        .apply("Check image transform condition", imageTransform.check(types))
        .apply("Interpret image", imageTransform.interpret())
        .apply("Write image to avro", imageTransform.write(pathFn));

    uniqueRecords
        .apply("Check audubon transform condition", audubonTransform.check(types))
        .apply("Interpret audubon", audubonTransform.interpret())
        .apply("Write audubon to avro", audubonTransform.write(pathFn));

    uniqueRecords
        .apply("Check measurement transform condition", measurementOrFactTransform.check(types))
        .apply("Interpret measurement", measurementOrFactTransform.interpret())
        .apply("Write measurement to avro", measurementOrFactTransform.write(pathFn));

    uniqueRecords
        .apply("Check collection attribution", alaAttributionTransform.check(types))
        .apply("Interpret collection attribution", alaAttributionTransform.interpret(metadataView))
        .apply("Write attribution to avro", alaAttributionTransform.write(pathFn));

    uniqueRecords
        .apply("Check ALA taxonomy transform condition", alaTaxonomyTransform.check(types))
        .apply("Interpret ALA taxonomy", alaTaxonomyTransform.interpret())
        .apply("Write ALA taxon to avro", alaTaxonomyTransform.write(pathFn));

    uniqueRecords
        .apply("Check location transform condition", locationTransform.check(types))
        .apply("Interpret location", locationTransform.interpret(metadataView))
        .apply("Write location to avro", locationTransform.write(pathFn));

    log.info("Running the pipeline");
    PipelineResult result = p.run();
    result.waitUntilFinish();

    MetricsHandler.saveCountersToTargetPathFile(options, result.metrics());

    log.info("Deleting beam temporal folders");
    String tempPath = String.join("/", targetPath, datasetId, attempt.toString());
    FsUtils.deleteDirectoryByPrefix(hdfsSiteConfig, tempPath, ".temp-beam");

    log.info("Pipeline has been finished");
  }
}
