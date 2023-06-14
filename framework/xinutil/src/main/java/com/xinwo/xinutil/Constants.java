package com.xinwo.xinutil;

import android.os.Environment;

public class Constants {
    public static final String TAG = "rance";
    public static final String AUTHORITY = "com.xjh.xinwo.fileprovider";
    /** 0x001-接受消息  0x002-发送消息**/
    public static final int CHAT_ITEM_TYPE_LEFT = 0x001;
    public static final int CHAT_ITEM_TYPE_RIGHT = 0x002;
    /** 0x003-发送中  0x004-发送失败  0x005-发送成功**/
    public static final int CHAT_ITEM_SENDING = 0x003;
    public static final int CHAT_ITEM_SEND_ERROR = 0x004;
    public static final int CHAT_ITEM_SEND_SUCCESS = 0x005;

    public static final String CHAT_FILE_TYPE_TEXT = "text";
    public static final String CHAT_FILE_TYPE_FILE = "file";
    public static final String CHAT_FILE_TYPE_IMAGE = "image";
    public static final String CHAT_FILE_TYPE_VOICE = "voice";
    public static final String CHAT_FILE_TYPE_CONTACT = "contact";
    public static final String CHAT_FILE_TYPE_LINK = "LINK";


    public static final int PERMISSIONS_REQUEST_STORAGE = 1;


    public static class ShowMsgActivity {
        public static final String STitle = "showmsg_title";
        public static final String SMessage = "showmsg_message";
        public static final String BAThumbData = "showmsg_thumb_data";
    }

    public static String videoPrePath = Environment.getExternalStorageDirectory() + "/Android/data/com.xjh.xinwo";
    public static String[] topVideos = {
            videoPrePath + "/girl_01_01.mp4",
            videoPrePath + "/girl_02_01.mp4",
            videoPrePath + "/girl_03_01.mp4",
            videoPrePath + "/girl_04_01.mp4",
            videoPrePath + "/girl_05_01.mp4",
            videoPrePath + "/girl_01_01.mp4",
            videoPrePath + "/girl_02_01.mp4",
            videoPrePath + "/girl_03_01.mp4",
            videoPrePath + "/girl_04_01.mp4",
            videoPrePath + "/girl_05_01.mp4",
    };


    public static String[] firstVideos = {
            videoPrePath + "/girl_01_01.mp4",
            videoPrePath + "/girl_01_02.mp4",
            videoPrePath + "/girl_01_03.mp4",
            videoPrePath + "/girl_01_04.mp4",
            videoPrePath + "/girl_01_01.mp4",
            videoPrePath + "/girl_01_02.mp4",
            videoPrePath + "/girl_01_03.mp4",
            videoPrePath + "/girl_01_04.mp4",
    };

    public static String[] secondVideos = {
            videoPrePath + "/girl_02_01.mp4",
            videoPrePath + "/girl_02_02.mp4",
            videoPrePath + "/girl_02_03.mp4",
            videoPrePath + "/girl_02_04.mp4",
            videoPrePath + "/girl_02_01.mp4",
            videoPrePath + "/girl_02_02.mp4",
            videoPrePath + "/girl_02_03.mp4",
            videoPrePath + "/girl_02_04.mp4",
    };

    public static String[] thirdVideos = {
            videoPrePath + "/girl_03_01.mp4",
            videoPrePath + "/girl_03_02.mp4",
            videoPrePath + "/girl_03_03.mp4",
            videoPrePath + "/girl_03_04.mp4",
            videoPrePath + "/girl_03_01.mp4",
            videoPrePath + "/girl_03_02.mp4",
            videoPrePath + "/girl_03_03.mp4",
            videoPrePath + "/girl_03_04.mp4",
    };

    public static String[] forthVideos = {
            videoPrePath + "/girl_04_01.mp4",
            videoPrePath + "/girl_04_02.mp4",
            videoPrePath + "/girl_04_03.mp4",
            videoPrePath + "/girl_04_04.mp4",
            videoPrePath + "/girl_04_01.mp4",
            videoPrePath + "/girl_04_02.mp4",
            videoPrePath + "/girl_04_03.mp4",
            videoPrePath + "/girl_04_04.mp4",
    };

    public static String[] fifthVideos = {
            videoPrePath + "/girl_05_01.mp4",
            videoPrePath + "/girl_05_02.mp4",
            videoPrePath + "/girl_05_03.mp4",
            videoPrePath + "/girl_05_04.mp4",
            videoPrePath + "/girl_05_01.mp4",
            videoPrePath + "/girl_05_02.mp4",
            videoPrePath + "/girl_05_03.mp4",
            videoPrePath + "/girl_05_04.mp4",
    };

    public static int[] topPics = {
            R.drawable.pic_girl_01_01,
            R.drawable.pic_girl_02_01,
            R.drawable.pic_girl_03_01,
            R.drawable.pic_girl_04_01,
            R.drawable.pic_girl_05_01,
            R.drawable.pic_girl_01_01,
            R.drawable.pic_girl_02_01,
            R.drawable.pic_girl_03_01,
            R.drawable.pic_girl_04_01,
            R.drawable.pic_girl_05_01,
    };

    public static int[] firstPics = {
            R.drawable.pic_girl_01_01,
            R.drawable.pic_girl_01_02,
            R.drawable.pic_girl_01_03,
            R.drawable.pic_girl_01_04,
    };

    public static int[] secondPics = {
            R.drawable.pic_girl_02_01,
            R.drawable.pic_girl_02_02,
            R.drawable.pic_girl_02_03,
            R.drawable.pic_girl_02_04,
    };

    public static int[] thirdPics = {
            R.drawable.pic_girl_03_01,
            R.drawable.pic_girl_03_02,
            R.drawable.pic_girl_03_03,
            R.drawable.pic_girl_03_04,
    };

    public static int[] forthPics = {
            R.drawable.pic_girl_04_01,
            R.drawable.pic_girl_04_02,
            R.drawable.pic_girl_04_03,
            R.drawable.pic_girl_04_04,
    };

    public static int[] fifthPics = {
            R.drawable.pic_girl_05_01,
            R.drawable.pic_girl_05_02,
            R.drawable.pic_girl_05_03,
            R.drawable.pic_girl_05_04,
    };

    public static String[][] allVideos = {
            firstVideos,
            secondVideos,
            thirdVideos,
            forthVideos,
            fifthVideos,
            firstVideos,
            secondVideos,
            thirdVideos,
            forthVideos,
            fifthVideos,
    };

    public static int[][] allPics = {
            firstPics,
            secondPics,
            thirdPics,
            forthPics,
            firstPics,
            firstPics,
            secondPics,
            thirdPics,
            forthPics,
            firstPics,
    };
}
