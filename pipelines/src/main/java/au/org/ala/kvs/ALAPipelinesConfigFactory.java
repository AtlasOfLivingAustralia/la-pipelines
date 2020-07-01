package au.org.ala.kvs;

import au.org.ala.utils.ALAFsUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import org.apache.hadoop.fs.FileSystem;
import org.gbif.pipelines.ingest.utils.FsUtils;
import org.gbif.pipelines.parsers.config.model.PipelinesConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Function;

import static org.gbif.pipelines.ingest.utils.FsUtils.getLocalFileSystem;

public class ALAPipelinesConfigFactory {

    private static volatile ALAPipelinesConfigFactory instance;

    private final ALAPipelinesConfig config;

    private static final Object MUTEX = new Object();

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    static {
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        MAPPER.findAndRegisterModules();
    }

    @SneakyThrows
    private ALAPipelinesConfigFactory(String hdfsSiteConfig, String propertiesPath) {
        this.config = ALAFsUtils.readConfigFile(hdfsSiteConfig, propertiesPath);
    }

    public static ALAPipelinesConfigFactory getInstance(String hdfsSiteConfig, String propertiesPath) {
        if (instance == null) {
            synchronized (MUTEX) {
                if (instance == null) {
                    instance = new ALAPipelinesConfigFactory(hdfsSiteConfig, propertiesPath);
                }
            }
        }
        return instance;
    }

    public ALAPipelinesConfig get() {
        return config;
    }

//    public static ALAPipelinesConfig read(Path path) {
//        Function<Path, InputStream> absolute =
//                p -> {
//                    try {
//                        return new FileInputStream(p.toFile());
//                    } catch (Exception ex) {
//                        String msg = "Properties with absolute p could not be read from " + path;
//                        throw new IllegalArgumentException(msg, ex);
//                    }
//                };
//
//        Function<Path, InputStream> resource =
//                p -> Thread.currentThread().getContextClassLoader().getResourceAsStream(p.toString());
//
//        Function<Path, InputStream> function = path.isAbsolute() ? absolute : resource;
//
//        try (InputStream in = function.apply(path)) {
//            // read properties from input stream
//            return MAPPER.readValue(in, ALAPipelinesConfig.class);
//        } catch (Exception ex) {
//            String msg = "Properties with absolute path could not be read from " + path;
//            throw new IllegalArgumentException(msg, ex);
//        }
//    }
}
