package com.xjh.xinwo.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AssetsUtils {
    private final String TAG = "AssetsUtils";

    private static AssetsUtils instance;
    private static final int SUCCESS = 1;
    private static final int FAILED = 0;
    private Context context;
    private FileOperateCallback callback;
    private volatile boolean isSuccess;
    private String errorStr;

    public static AssetsUtils getInstance(Context context) {
        if (instance == null) {
            instance = new AssetsUtils(context);
        }
        return instance;
    }

    private AssetsUtils(Context context) {
        this.context = context;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (callback != null) {
                if (msg.what == SUCCESS) {
                    callback.onSuccess();
                }
                if (msg.what == FAILED) {
                    callback.onFailed(msg.obj.toString());
                }
            }
        }
    };

    public AssetsUtils copyAssetsToSD(final String srcPath, final String sdPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAssetsToDst(context, srcPath, sdPath);
                if (isSuccess) {
                    handler.obtainMessage(SUCCESS).sendToTarget();
                } else {
                    handler.obtainMessage(FAILED, errorStr).sendToTarget();
                }
            }
        }).start();
        return this;
    }

    public void setFileOperateCallback(FileOperateCallback callback) {
        this.callback = callback;
    }

    private void copyAssetsToDst(Context context, String srcPath, String dstPath) {
        Log.d(TAG, "srcPath = " + srcPath);
        Log.d(TAG, "dstPath = " + dstPath);
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(dstPath);
                if (!file.exists()) {
                    file.mkdirs();
                    Log.e(TAG, "建立文件 file = " + file.toString());
                }
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        Log.e(TAG, "assets 文件夹下的目录 srcPath = " + srcPath);
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        Log.e(TAG, "assets 文件夹 fileName = " + fileName);
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {


                File outFile = new File(dstPath);
                Log.e(TAG, "拷贝文件 ================= outFile = " + outFile.toString());
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            errorStr = e.getMessage();
            isSuccess = false;
        }
    }

    public int getVideoCount(String dstPath) {
        File file = new File(dstPath);

        Log.e(TAG, "getVideoCount --> dstPath = " + dstPath);
        Log.e(TAG, "getVideoCount --> file = " + file + "    exists = " + file.exists() + "   isDirectory = " + file.isDirectory());

        if (file.exists()) {
            if (file.isDirectory()) {
                String[] list = file.list();
                Log.e(TAG, "getVideoCount --> list = " + list);
                if (list != null) {
                    Log.e(TAG, "getVideoCount --> list.length = " + list.length);
                    return list.length;
                } else {
                    return 0;
                }
            }
        }

        return 0;
    }

    public interface FileOperateCallback {
        void onSuccess();

        void onFailed(String error);
    }

}