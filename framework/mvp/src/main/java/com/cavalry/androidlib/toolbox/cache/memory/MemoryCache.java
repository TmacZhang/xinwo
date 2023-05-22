package com.cavalry.androidlib.toolbox.cache.memory;

/**
 * 缓存http数据
 * @author Cavalry Lin
 * @since 1.0.0
 */

public interface MemoryCache<K,V> {
    V put(K key, V value);
    V get(K key);
    V remove(K key);
    void clear();
}
