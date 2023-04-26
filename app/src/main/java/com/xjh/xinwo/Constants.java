package com.xjh.xinwo;

import android.os.Environment;

public class Constants {
	// APP_ID 替换为你的应用从官方网站申请到的合法appID
	public static final String APP_ID = "wxa78ebfd006b71da9";

	public static  final String JWT_CODE = "jwt_code";

	public static final int PERMISSIONS_REQUEST_STORAGE = 1;

	public static final String NIM_LOGIN_INFO_ACCOUNT = "nim_login_info_acount";
	public static final String NIM_LOGIN_INFO_TOKEN = "nim_login_info_token";
	public static final String NIM_LOGIN_INFO_APPKEY = "nim_login_info_appkey";

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

	public static int[]  secondPics = {
			R.drawable.pic_girl_02_01,
			R.drawable.pic_girl_02_02,
			R.drawable.pic_girl_02_03,
			R.drawable.pic_girl_02_04,
	};

	public static int[]  thirdPics = {
			R.drawable.pic_girl_03_01,
			R.drawable.pic_girl_03_02,
			R.drawable.pic_girl_03_03,
			R.drawable.pic_girl_03_04,
	};

	public static int[]  forthPics = {
			R.drawable.pic_girl_04_01,
			R.drawable.pic_girl_04_02,
			R.drawable.pic_girl_04_03,
			R.drawable.pic_girl_04_04,
	};

	public static int[]  fifthPics = {
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
