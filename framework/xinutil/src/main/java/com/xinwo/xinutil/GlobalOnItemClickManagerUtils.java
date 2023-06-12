package com.xinwo.xinutil;

import android.content.Context;

/**
 * 点击表情的全局监听管理类
 */
public class GlobalOnItemClickManagerUtils {
    private static GlobalOnItemClickManagerUtils instance;
    public static GlobalOnItemClickManagerUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (GlobalOnItemClickManagerUtils.class) {
                if (instance == null) {
                    instance = new GlobalOnItemClickManagerUtils();
                }
            }
        }
        return instance;
    }

}
