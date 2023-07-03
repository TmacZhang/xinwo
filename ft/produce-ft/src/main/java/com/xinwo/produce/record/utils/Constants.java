package com.xinwo.produce.record.utils;

import android.content.Context;

public class Constants {
    public static String getVideoRecordPath(Context context){
        return context.getFilesDir() + "/record";
    }

    public static String getVideoRecordTmpPath(Context context){
        return getVideoRecordPath(context) + "/tmp";
    }
}
