package com.cavalry.androidlib.toolbox.cache.memory.impl;

import android.util.Log;

import com.cavalry.androidlib.toolbox.cache.memory.MemoryCache;
import com.cavalry.androidlib.toolbox.utils.LibLog;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 使用软引用存储值
 * @author Cavalry Lin
 * @since 1.0.0
 */
public abstract class SoftLruCache<K,V> implements MemoryCache<K,V> {
    private final String TAG = "SoftLruCache";

    protected final LinkedHashMap<K, SoftReference<V>> map;
    protected long size;
    protected long maxSize;

    public SoftLruCache(long maxSize){
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<K, SoftReference<V>>(0, 0.75f, true);
    }

    @Override
    public V put(K key, V value) {
        LibLog.d(TAG,"put");

        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }

        SoftReference<V> previous;
//        synchronized (this) {
            size += safeSizeOf(key,value);
            SoftReference<V> sValue = new SoftReference<V>(value);
            previous = map.put(key, sValue);
            if (previous != null) {
                size -= safeSizeOf(key, previous!=null ? previous.get() : null);
            }
//        }

        trimToSize(maxSize);
        return previous!=null ? previous.get() : null;
    }



    @Override
    public V get(K key) {
        LibLog.d(TAG,"get");

        if (key == null) {
            throw new NullPointerException("key == null");
        }


        SoftReference<V> sValue;
//        synchronized (this) {
            sValue = map.get(key);
            if (sValue != null) {
                return sValue.get();
            }
//        }
        return null;
    }

    @Override
    public V remove(K key) {
        LibLog.d(TAG,"remove");

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        SoftReference<V> previous;
//        synchronized (this) {
            previous = map.remove(key);
            if (previous != null) {
                size -= safeSizeOf(key, previous.get());
            }
//        }

        return previous!=null ? previous.get() : null;
    }

    @Override
    public void clear() {
        LibLog.d(TAG,"clear");

        map.clear();
    }

    private long safeSizeOf(K key, V value){
        LibLog.d(TAG,"safeSizeOf");
        if(value==null)
            return 0l;

        long result = sizeOf(key, value);
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + key + "=" + value);
        }
        return result;
    }

    private void trimToSize(long maxSize) {
        LibLog.d(TAG,"trimToSize, size="+size+" ,maxSize="+maxSize);

        while (true) {
            K key;
            SoftReference<V> sValue;
//            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName()
                            + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) {
                    break;
                }

                Map.Entry<K, SoftReference<V>> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                sValue = toEvict.getValue();
                map.remove(key);
                size -= safeSizeOf(key, sValue!=null ? sValue.get() : null);
//            }

        }
    }

    protected abstract long sizeOf(K key, V value);

}
