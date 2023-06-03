package com.xinwo.cache;


/**
 * 缓存http数据
 * @author Cavalry Lin
 * @since 1.0.0
 */
public interface DiskCache<K,V> {
    boolean save(K key,V value);
    V get(K key);
    boolean remove(K key);
    long getLastModified(String key);
    void clear();
    void close();

}
