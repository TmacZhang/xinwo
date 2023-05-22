package com.cavalry.androidlib.mvp.service;

import com.cavalry.androidlib.net.DefaultNetManager;

import okhttp3.OkHttpClient;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class DefualtServiceGenerator extends LibServiceGenerator {

    private static IServiceGenerator mServiceGenerator;

    private DefualtServiceGenerator(){}

    public static IServiceGenerator getInstance(){
        if(mServiceGenerator==null){
            synchronized (DefualtServiceGenerator.class){
                if(mServiceGenerator==null){
                    mServiceGenerator = new DefualtServiceGenerator();
                }
            }
        }
        return mServiceGenerator;
    }

    public OkHttpClient initOkHttpClient(){
        return DefaultNetManager.getOkHttpClient();
    }
}
