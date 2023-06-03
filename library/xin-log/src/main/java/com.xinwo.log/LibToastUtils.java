package com.xinwo.log;

import android.content.Context;
import android.widget.Toast;

import com.xinwo.application.XinApplicationUtil;

public class LibToastUtils {

    public static void toast(String text) {
        Toast.makeText(XinApplicationUtil.Companion.getInstance().getMApplication(), text, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void toastOnUiThread(final Context context, final String text) {
        HandlerManager.getInstance().post(new Runnable() {
            @Override
            public void run() {
                toast(text);
            }
        });
    }

    public static void toastOnUiThreadLong(final Context context, final String text) {
        HandlerManager.getInstance().post(new Runnable() {
            @Override
            public void run() {
                toastLong(context, text);
            }
        });
    }
}
