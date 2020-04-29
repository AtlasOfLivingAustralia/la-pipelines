package au.org.ala.utils;

import org.gbif.pipelines.common.PipelinesVariables;
import org.gbif.pipelines.ingest.options.BasePipelineOptions;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.utils.FsUtils;

/**
 * Extensions to FSUtils.
 *
 * @see FsUtils
 */
public class ALAFsUtils {

    /**
     * Constructs the path for reading / writing identifiers. This is written outside of /interpreted directory.
     *
     * Example /data/pipelines-data/dr893/1/identifiers/ala_uuid where name = 'ala_uuid'
     *
     * @param options
     * @param name
     * @param uniqueId
     * @return
     */
    public static String buildPathIdentifiersUsingTargetPath(BasePipelineOptions options, String name, String uniqueId) {
        return FsUtils.buildPath(FsUtils.buildDatasetAttemptPath(options, "identifiers", false), name, "interpret-" + uniqueId).toString();
    }

    /**
     * Constructs the path for reading / writing sampling. This is written outside of /interpreted directory.
     *
     * Example /data/pipelines-data/dr893/1/sampling/ala_uuid where name = 'ala_uuid'
     *
     * @param options
     * @param name
     * @param uniqueId
     * @return
     */
    public static String buildPathSamplingUsingTargetPath(BasePipelineOptions options, String name, String uniqueId) {
        return FsUtils.buildPath(FsUtils.buildDatasetAttemptPath(options, "sampling", false), name, name + "-" + uniqueId).toString();
    }

    public static String buildPathSamplingOutputUsingTargetPath(InterpretationPipelineOptions options) {
        return FsUtils.buildPath(
                FsUtils.buildDatasetAttemptPath(options, "sampling", false),
                PipelinesVariables.Pipeline.Interpretation.RecordType.AUSTRALIA_SPATIAL.toString().toLowerCase(),
                PipelinesVariables.Pipeline.Interpretation.RecordType.AUSTRALIA_SPATIAL.toString().toLowerCase()
        ).toString();
    }

    public static String buildPathSamplingDownloadsUsingTargetPath(InterpretationPipelineOptions options) {
        return FsUtils.buildPath(FsUtils.buildDatasetAttemptPath(options, "sampling", false), "downloads").toString();
    }
}
