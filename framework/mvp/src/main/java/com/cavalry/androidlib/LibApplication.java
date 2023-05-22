package com.cavalry.androidlib;

import android.app.Application;
import android.content.Context;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class LibApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
