package au.org.ala.kvs.cache;

import org.gbif.kvs.KeyValueStore;

import java.util.Map;

public interface WarmableCache<K,V> extends KeyValueStore<K, V> {

    void put(K key, V value);
    void putAll(Map<K,V> map);
}
