package au.org.ala.kvs;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import org.gbif.pipelines.parsers.config.model.KvConfig;

/**
 * @TODO consolidate with KvConfig in GBIF's codebase.
 */
@Builder
@Data
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

  GeocodeShpIntersectConfig geocodeShpIntersectConfig;
  //take GBIF config
  @Setter private KvConfig commonConfig;




}
