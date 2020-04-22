package au.org.ala.kvs.cache;

import org.gbif.kvs.KeyValueStore;

public interface WarmableCache<K,V> extends KeyValueStore<K, V> {

    void put(K key, V value);
}
