package com.xinwo.cache.impl;

import com.xinwo.log.LibLog;

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
