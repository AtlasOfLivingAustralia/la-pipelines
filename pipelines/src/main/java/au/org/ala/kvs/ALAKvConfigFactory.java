package au.org.ala.kvs;

import lombok.NonNull;

import java.util.Properties;

public class ALAKvConfigFactory {

    public static final String ALA_TAXONOMY_PREFIX = "ala_taxonomy";
    public static final String ALA_SPATIAL_PREFIX = "ala_spatial";
    private static final String WS_API_BASE_PATH_PROP = ".api.url";

    public static ALAKvConfig create(@NonNull Properties props, @NonNull String prefix) {
        String taxonomyBasePath = props.getProperty(ALA_TAXONOMY_PREFIX + WS_API_BASE_PATH_PROP);
        String spatialBasePath = props.getProperty(ALA_SPATIAL_PREFIX + WS_API_BASE_PATH_PROP);
        return ALAKvConfig.builder()
                .taxonomyBasePath(taxonomyBasePath)
                .spatialBasePath(spatialBasePath)
                .build();
    }
}
