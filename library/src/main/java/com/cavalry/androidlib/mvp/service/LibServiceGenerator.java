package com.cavalry.androidlib.mvp.service;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * <p>子类需覆写{@link #initOkHttpClient()}来实现OkHttpClient的初始化,可参照{@link com.cavalry.androidlib.mvp.service.DefualtServiceGenerator}</p>
 *
 * @author Cavalry Lin
 * @since 1.0.0
 */

public abstract class LibServiceGenerator implements IServiceGenerator {
    private static Retrofit mRetrofit;

    @Override
    public final <S> S createService(Class<S> serviceClass) {
        return getRetrofit().create(serviceClass);
    }

    private Retrofit getRetrofit() {

        if (mRetrofit == null) {
            mRetrofit = initRetrofit(initOkHttpClient());
        }
        return mRetrofit;
    }

    private Retrofit initRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//让Retrofit支持RxJava
                .baseUrl("http://www.fake-host.com/")//为了检测通过,随便传一个假地址,使用Service时应传递完整路径即可将此baseUrl替换
                .build();
    }


}