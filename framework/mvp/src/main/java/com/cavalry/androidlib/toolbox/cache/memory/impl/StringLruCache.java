package com.cavalry.androidlib.toolbox.cache.memory.impl;

import com.cavalry.androidlib.toolbox.utils.LibLog;

/**
 * 使用软引用存储String类型的value
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class StringLruCache extends SoftLruCache<String,String> {


    public StringLruCache(long maxSize) {
        super(maxSize);
    }

    @Override
    protected long sizeOf(String key, String value) {
        LibLog.e("sizeOf","value="+value);
        if(value==null)
            return 0;

        byte[] bytes = value.getBytes();
        return bytes.length;
    }
}
