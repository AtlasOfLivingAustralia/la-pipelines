package au.org.ala.kvs;

import lombok.NonNull;

import java.util.Properties;

public class ALAKvConfigFactory {

    public static final String GEOCODE_PREFIX = "geocode";
    public static final String ALA_TAXONOMY_PREFIX = "ala_taxonomy";
    public static final String ALA_SPATIAL_PREFIX = "ala_spatial";
    public static final String ALA_COLLECTORY_PREFIX = "ala_collectory";
    public static final String ALA_LISTS_PREFIX = "ala_lists";
    private static final String WS_API_BASE_PATH_PROP = ".api.url";
    private static final String KV_CACHE_DIRECTORY_PATH_PROP = "kv_cache_directory";
    private static final String USE_MAPDB_PROP = "use_mapdb";

    public static ALAKvConfig create(@NonNull Properties props) {

        //API URLS
        String taxonomyBasePath = props.getProperty(ALA_TAXONOMY_PREFIX + WS_API_BASE_PATH_PROP);
        String spatialBasePath = props.getProperty(ALA_SPATIAL_PREFIX + WS_API_BASE_PATH_PROP);
        String collectoryBasePath = props.getProperty(ALA_COLLECTORY_PREFIX + WS_API_BASE_PATH_PROP);
        String listsBasePath = props.getProperty(ALA_LISTS_PREFIX + WS_API_BASE_PATH_PROP);
        String geocodeBasePath = props.getProperty(GEOCODE_PREFIX + WS_API_BASE_PATH_PROP);
        Boolean mapDBCacheEnabled = Boolean.parseBoolean(props.getProperty(USE_MAPDB_PROP, "false"));

        //Cache directory
        String cacheDirectoryPath = props.getProperty(KV_CACHE_DIRECTORY_PATH_PROP, "/data/pipelines-cache");

        return ALAKvConfig.builder()
                .taxonomyBasePath(taxonomyBasePath)
                .spatialBasePath(spatialBasePath)
                .collectoryBasePath(collectoryBasePath)
                .listsBasePath(listsBasePath)
                .geocodeBasePath(geocodeBasePath)
                .cacheDirectoryPath(cacheDirectoryPath)
                .mapDBCacheEnabled(mapDBCacheEnabled)
                .build();
    }
}
