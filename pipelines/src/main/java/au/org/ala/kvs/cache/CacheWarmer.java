package au.org.ala.kvs.cache;

import org.gbif.kvs.KeyValueStore;

public interface CacheWarmer<K,V> {
    void warmCache(WarmableCache<K,V> kvs);
}
