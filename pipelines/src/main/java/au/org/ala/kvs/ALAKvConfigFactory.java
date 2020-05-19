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

    private static final String GEOCODE_CACHE_MAX_SIZE = "geocode_cache_max_size";
    private static final String TAXONOMY_CACHE_MAX_SIZE = "taxonomy_cache_max_size";
    private static final String COLLECTION_CACHE_MAX_SIZE = "collection_cache_max_size";
    private static final String METADATA_CACHE_MAX_SIZE = "metadata_cache_max_size";

    private static final String GEOCODE_CACHE_PERSIST_ENABLED = "geocode_cache_persist_enabled";
    private static final String TAXONOMY_CACHE_PERSIST_ENABLED = "taxonomy_cache_persist_enabled";
    private static final String COLLECTION_CACHE_PERSIST_ENABLED = "collection_cache_persist_enabled";
    private static final String METADATA_CACHE_PERSIST_ENABLED = "metadata_cache_persist_enabled";

    private static final String GEOCODE_CACHE_FILENAME = "geocode_cache_filename";
    private static final String TAXONOMY_CACHE_FILENAME = "taxonomy_cache_filename";
    private static final String COLLECTION_CACHE_FILENAME = "collection_cache_filename";
    private static final String METADATA_CACHE_FILENAME = "metadata_cache_filename";


    public static ALAKvConfig create(@NonNull Properties props) {

        //API URLS
        String taxonomyBasePath = props.getProperty(ALA_TAXONOMY_PREFIX + WS_API_BASE_PATH_PROP);
        String spatialBasePath = props.getProperty(ALA_SPATIAL_PREFIX + WS_API_BASE_PATH_PROP);
        String collectoryBasePath = props.getProperty(ALA_COLLECTORY_PREFIX + WS_API_BASE_PATH_PROP);
        String listsBasePath = props.getProperty(ALA_LISTS_PREFIX + WS_API_BASE_PATH_PROP);
        String geocodeBasePath = props.getProperty(GEOCODE_PREFIX + WS_API_BASE_PATH_PROP);

        Boolean geocodeCachePersistEnabled = Boolean.parseBoolean(props.getProperty(GEOCODE_CACHE_PERSIST_ENABLED, "false"));
        Boolean taxonomyCachePersistEnabled = Boolean.parseBoolean(props.getProperty(TAXONOMY_CACHE_PERSIST_ENABLED, "false"));
        Boolean collectionCachePersistEnabled = Boolean.parseBoolean(props.getProperty(COLLECTION_CACHE_PERSIST_ENABLED, "false"));
        Boolean metadataCachePersistEnabled = Boolean.parseBoolean(props.getProperty(METADATA_CACHE_PERSIST_ENABLED, "false"));

        long geocodeCacheMaxSize = Long.parseLong(props.getProperty(GEOCODE_CACHE_MAX_SIZE, "100000"));
        long taxonomyCacheMaxSize = Long.parseLong(props.getProperty(TAXONOMY_CACHE_MAX_SIZE, "100000"));
        long collectionCacheMaxSize = Long.parseLong(props.getProperty(COLLECTION_CACHE_MAX_SIZE, "100000"));
        long metadataCacheMaxSize = Long.parseLong(props.getProperty(METADATA_CACHE_MAX_SIZE, "100000"));

        String geocodeCacheFileName = props.getProperty(GEOCODE_CACHE_FILENAME, "geocode");
        String taxonomyCacheFileName = props.getProperty(TAXONOMY_CACHE_FILENAME, "taxonomy");
        String collectionCacheFileName = props.getProperty(COLLECTION_CACHE_FILENAME, "collection");
        String metadataCacheFileName = props.getProperty(METADATA_CACHE_FILENAME, "metadata");

        //Cache directory
        String cacheDirectoryPath = props.getProperty(KV_CACHE_DIRECTORY_PATH_PROP, "/data/pipelines-cache");

        return ALAKvConfig.builder()
                .taxonomyBasePath(taxonomyBasePath)
                .spatialBasePath(spatialBasePath)
                .collectoryBasePath(collectoryBasePath)
                .listsBasePath(listsBasePath)
                .geocodeBasePath(geocodeBasePath)
                .cacheDirectoryPath(cacheDirectoryPath)
                .geocodeCachePersistEnabled(geocodeCachePersistEnabled)
                .taxonomyCachePersistEnabled(taxonomyCachePersistEnabled)
                .collectionCachePersistEnabled(collectionCachePersistEnabled)
                .metadataCachePersistEnabled(metadataCachePersistEnabled)
                .geocodeCacheMaxSize(geocodeCacheMaxSize)
                .taxonomyCacheMaxSize(taxonomyCacheMaxSize)
                .collectionCacheMaxSize(collectionCacheMaxSize)
                .metadataCacheMaxSize(metadataCacheMaxSize)
                .geocodeCacheFileName(geocodeCacheFileName)
                .taxonomyCacheFileName(taxonomyCacheFileName)
                .collectionCacheFileName(collectionCacheFileName)
                .metadataCacheFileName(metadataCacheFileName)
                .build();
    }
}
