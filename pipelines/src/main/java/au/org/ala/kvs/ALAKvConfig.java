package au.org.ala.kvs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * @TODO consolidate with KvConfig in GBIF's codebase.
 */
@Value
@Builder
@AllArgsConstructor
public class ALAKvConfig implements Serializable {

  String geocodeBasePath;
  String taxonomyBasePath;
  String spatialBasePath;
  String collectoryBasePath;
  String listsBasePath;
  long timeout;

  long geocodeCacheMaxSize;
  long taxonomyCacheMaxSize;
  long collectionCacheMaxSize;
  long metadataCacheMaxSize;

  boolean geocodeCachePersistEnabled;
  boolean taxonomyCachePersistEnabled;
  boolean collectionCachePersistEnabled;
  boolean metadataCachePersistEnabled;

  String geocodeCacheFileName;
  String taxonomyCacheFileName;
  String collectionCacheFileName;
  String metadataCacheFileName;

  String cacheDirectoryPath;
}
