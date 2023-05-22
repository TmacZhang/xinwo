package com.cavalry.androidlib.toolbox.managers;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class HandlerManager {
    private static Handler mHandler;

    public static Handler getInstance(){
        if(mHandler==null){
            synchronized (HandlerManager.class){
                if(mHandler==null){
                    mHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mHandler;
    }
}
