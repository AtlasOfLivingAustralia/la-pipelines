package au.org.ala.utils;

import org.gbif.pipelines.ingest.options.BasePipelineOptions;
import org.gbif.pipelines.ingest.utils.FsUtils;

/**
 * Extensions to FSUtils.
 *
 * @see FsUtils
 */
public class ALAFsUtils {

    public static String buildPathIdentifiersUsingTargetPath(BasePipelineOptions options, String name, String uniqueId) {
        return FsUtils.buildPath(FsUtils.buildDatasetAttemptPath(options, "identifiers", false), name, "interpret-" + uniqueId).toString();
    }

    public static String buildPathSamplingUsingTargetPath(BasePipelineOptions options, String name, String uniqueId) {
        return FsUtils.buildPath(FsUtils.buildDatasetAttemptPath(options, "sampling", false), name, "interpret-" + uniqueId).toString();
    }
}
