package com.xjh.xinwo.mvp.service;

import com.cavalry.androidlib.mvp.service.DefualtServiceGenerator;
import com.cavalry.androidlib.mvp.service.IServiceGenerator;
import com.cavalry.androidlib.mvp.service.LibServiceGenerator;
import com.xjh.xinwo.manager.NetManager;

import okhttp3.OkHttpClient;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class ServiceGenerator extends LibServiceGenerator {

    private static ServiceGenerator mServiceGenerator;

    private ServiceGenerator(){}

    public static IServiceGenerator getInstance(){
        if(mServiceGenerator==null){
            synchronized (DefualtServiceGenerator.class){
                if(mServiceGenerator==null){
                    mServiceGenerator = new ServiceGenerator();
                }
            }
        }
        return mServiceGenerator;
    }

    @Override
    public OkHttpClient initOkHttpClient() {
        return NetManager.getOkHttpClient();
    }
}
