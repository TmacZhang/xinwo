package com.xjh.gestureheart.record.utils;

import android.os.Environment;

/**
 * Created by 25623 on 2018/8/23.
 */

public class Constants {
    public static final String VIDEO_PATH = Environment.getExternalStorageDirectory() + "/Codec";

    public static final String VIDEO_RECORD_PATH = VIDEO_PATH + "/record";

    public static final String VIDEO_REACORD_TMP_PATH = VIDEO_RECORD_PATH + "/tmp";
}
