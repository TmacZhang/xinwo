package com.xinwo.network;

import okhttp3.OkHttpClient;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public interface IServiceGenerator {
    <S> S createService(Class<S> serviceClass);
    OkHttpClient initOkHttpClient();
}
