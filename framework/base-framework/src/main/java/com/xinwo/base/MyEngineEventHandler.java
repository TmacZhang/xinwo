package com.xinwo.base;

import android.content.Context;

import java.util.concurrent.ConcurrentHashMap;

public class MyEngineEventHandler {
    public MyEngineEventHandler(Context ctx, EngineConfig config) {
        this.mConfig = config;
        this.mContext = ctx;
    }

    private final EngineConfig mConfig;
    private final Context mContext;
    private final ConcurrentHashMap<AGEventHandler, Integer> mEventHandleList = new ConcurrentHashMap<>();

    public void addEventHandler(AGEventHandler handler) {
        this.mEventHandleList.put(handler, 0);
    }

    public void removeEventHandler(AGEventHandler handler) {
        this.mEventHandleList.remove(handler);
    }

}
