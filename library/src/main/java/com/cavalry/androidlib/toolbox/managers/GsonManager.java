package com.cavalry.androidlib.toolbox.managers;

import com.cavalry.androidlib.toolbox.managers.gsonadapter.DoubleDefault0Adapter;
import com.cavalry.androidlib.toolbox.managers.gsonadapter.IntegerDefault0Adapter;
import com.cavalry.androidlib.toolbox.managers.gsonadapter.LongDefault0Adapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class GsonManager {
    private GsonManager(){}

    private static GsonPool gsonPool = new GsonPool(8);

    public static Gson getGson(){
        return gsonPool.acquire();
    }

    public static void recycleGson(Gson gson){
        gsonPool.release(gson);
    }


    static class GsonPool{
        private final Object[] mPool;
        private int mPoolSize;

        /**
         * Creates a new instance.
         *
         * @param maxPoolSize The max pool size.
         *
         * @throws IllegalArgumentException If the max pool size is less than zero.
         */
        public GsonPool(int maxPoolSize) {
            if (maxPoolSize <= 0) {
                throw new IllegalArgumentException("The max pool size must be > 0");
            }
            mPool = new Object[maxPoolSize];
        }

        public Gson acquire() {
            if (mPoolSize > 0) {
                final int lastPooledIndex = mPoolSize - 1;
                Gson instance = (Gson) mPool[lastPooledIndex];
                mPool[lastPooledIndex] = null;
                mPoolSize--;
                return instance;
            }
            return new GsonBuilder()
                    .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                    .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                    .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                    .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                    .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                    .registerTypeAdapter(long.class, new LongDefault0Adapter())
                    .create();
        }

        public boolean release(Gson instance) {
            if (isInPool(instance)) {
                throw new IllegalStateException("Already in the pool!");
            }
            if (mPoolSize < mPool.length) {
                mPool[mPoolSize] = instance;
                mPoolSize++;
                return true;
            }
            return false;
        }

        private boolean isInPool(Gson instance) {
            for (int i = 0; i < mPoolSize; i++) {
                if (mPool[i] == instance) {
                    return true;
                }
            }
            return false;
        }

    }

}
