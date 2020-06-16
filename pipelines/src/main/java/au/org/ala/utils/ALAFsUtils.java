package au.org.ala.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.*;
import org.gbif.pipelines.common.PipelinesVariables;
import org.gbif.pipelines.ingest.options.BasePipelineOptions;
import org.gbif.pipelines.ingest.options.InterpretationPipelineOptions;
import org.gbif.pipelines.ingest.utils.FileSystemFactory;
import org.gbif.pipelines.ingest.utils.FsUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;

import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.Interpretation.DIRECTORY_NAME;
import static org.gbif.pipelines.common.PipelinesVariables.Pipeline.Interpretation.RecordType.ALL;

/**
 * Extensions to FSUtils.
 *
 * @see FsUtils
 */
@Slf4j
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

    /**
     * Helper method to get file system based on provided configuration.
     */
    @SneakyThrows
    public static FileSystem getFileSystem(String hdfsSiteConfig, String coreSiteConfig, String path) {
        return FileSystemFactory.getInstance(hdfsSiteConfig, coreSiteConfig, path).getFs(path);
    }

    /**
     * Removes a directory with content if the folder exists
     *
     * @param directoryPath path to some directory
     */
    public static boolean deleteIfExist(FileSystem fs, String directoryPath) {
        Path path = new Path(directoryPath);
        try {
            return fs.exists(path) && fs.delete(path, true);
        } catch (IOException e) {
            log.error("Can't delete {} directory, cause - {}", directoryPath, e.getCause());
            return false;
        }
    }

    /**
     * Removes a directory with content if the folder exists
     *
     * @param hdfsSiteConfig path to hdfs-site.xml config file
     * @param directoryPath path to some directory
     */
    public static boolean deleteIfExist(String hdfsSiteConfig, String coreSiteConfig, String directoryPath) {
        FileSystem fs = getFileSystem(hdfsSiteConfig, coreSiteConfig, directoryPath);
        Path path = new Path(directoryPath);
        try {
            return fs.exists(path) && fs.delete(path, true);
        } catch (IOException e) {
            log.error("Can't delete {} directory, cause - {}", directoryPath, e.getCause());
            return false;
        }
    }    

    /**
     * Helper method to write/overwrite a file
     */
    public static WritableByteChannel createByteChannel(FileSystem fs, String path) throws IOException {
       FSDataOutputStream stream = fs.create(new Path(path), true);
       return Channels.newChannel(stream);
    }

    /**
     * Helper method to write/overwrite a file
     */
    public static OutputStream openOutputStream(FileSystem fs, String path) throws IOException {
        return fs.create(new Path(path), true);
    }

    /**
     * Helper method to write/overwrite a file
     */
    public static ReadableByteChannel openByteChannel(FileSystem fs, String path) throws IOException {
        FSDataInputStream stream = fs.open(new Path(path));
        return Channels.newChannel(stream);
    }

    /**
     * Helper method to write/overwrite a file
     */
    public static InputStream openInputStream(FileSystem fs, String path) throws IOException {
        return fs.open(new Path(path));
    }

    public static boolean exists(FileSystem fs, String directoryPath) throws IOException {
        Path path = new Path(directoryPath);
        return fs.exists(path);
    }

    public static Collection<String> listPaths(FileSystem fs, String directoryPath) throws IOException {

        Path path = new Path(directoryPath);
        RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(path, false);
        List<String> filePaths = new ArrayList<String>();
        while (iterator.hasNext()){
            LocatedFileStatus locatedFileStatus = iterator.next();
            Path filePath = locatedFileStatus.getPath();
            filePaths.add(filePath.toString());
        }
        return filePaths;
    }

    /**
     * Read a properties file from HDFS/Local FS
     *
     * @param hdfsSiteConfig HDFS config file
     * @param filePath properties file path
     */
    @SneakyThrows
    public static Properties readPropertiesFile(String hdfsSiteConfig, String coreSiteConfig, String filePath) {
        FileSystem fs = getFileSystem(hdfsSiteConfig, coreSiteConfig, filePath);
        Path fPath = new Path(filePath);
        if (fs.exists(fPath)) {
            log.info("Reading properties path - {}", filePath);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(fPath)))) {
                Properties props = new Properties();
                props.load(br);
                log.info("Loaded properties - {}", props);
                return props;
            }
        }
        throw new FileNotFoundException("The properties file doesn't exist - " + filePath);
    }

    /**
     * Deletes directories if a dataset with the same attempt was interpreted before
     */
    public static void deleteInterpretIfExist(String hdfsSiteConfig, String coreSiteConfig, String basePath, String datasetId, Integer attempt,
                                              Set<String> steps) {
        if (steps != null && !steps.isEmpty()) {

            String path = String.join("/", basePath, datasetId, attempt.toString(), DIRECTORY_NAME);

            if (steps.contains(ALL.name())) {
                log.info("Delete interpretation directory - {}", path);
                boolean isDeleted = deleteIfExist(hdfsSiteConfig, coreSiteConfig, path);
                log.info("Delete interpretation directory - {}, deleted - {}", path, isDeleted);
            } else {
                for (String step : steps) {
                    log.info("Delete {}/{} directory", path, step.toLowerCase());
                    boolean isDeleted = deleteIfExist(hdfsSiteConfig, coreSiteConfig, String.join("/", path, step.toLowerCase()));
                    log.info("Delete interpretation directory - {}, deleted - {}", path, isDeleted);
                }
            }
        }
    }    
}
