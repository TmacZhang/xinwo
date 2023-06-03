package com.xinwo.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class DefaultNetManager {
    private final static int DEFAULT_TIMEOUT = 10;

    private static OkHttpClient mNetInterceptorClient;
    private static NetInterceptor mNetInterceptor = new NetInterceptor();

    /**
     * 默认添加网络拦截器
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        if(mNetInterceptorClient == null){
            synchronized (DefaultNetManager.class){
                if(mNetInterceptorClient==null){
                    mNetInterceptorClient = getOkHttpClientBuilder()
                            .addInterceptor(mNetInterceptor)
                            .build();
                }
            }
        }
        return mNetInterceptorClient;
    }

    private static OkHttpClient.Builder getOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        return builder;
    }

}
