package au.org.ala.kvs;

import lombok.NonNull;

import java.util.Properties;

public class ALAKvConfigFactory {

    public static final String ALA_TAXONOMY_PREFIX = "ala_taxonomy";
    private static final String WS_API_BASE_PATH_PROP = ".api.url";

    public static ALAKvConfig create(@NonNull Properties props, @NonNull String prefix) {
        String basePath = props.getProperty(prefix + WS_API_BASE_PATH_PROP);
        return ALAKvConfig.builder().taxonomyBasePath(basePath).build();
    }
}
