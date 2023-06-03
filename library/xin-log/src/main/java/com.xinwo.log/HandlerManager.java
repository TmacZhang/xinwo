package com.xinwo.log;

import android.os.Handler;
import android.os.Looper;

public class HandlerManager {
    private static Handler sHandler;

    public static Handler getInstance(){
        if(sHandler ==null){
            synchronized (HandlerManager.class){
                if(sHandler ==null){
                    sHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return sHandler;
    }
}
