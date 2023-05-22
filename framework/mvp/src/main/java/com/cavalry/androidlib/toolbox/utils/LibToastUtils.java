package com.cavalry.androidlib.toolbox.utils;

import android.widget.Toast;

import com.cavalry.androidlib.LibApplication;
import com.cavalry.androidlib.toolbox.managers.HandlerManager;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class LibToastUtils {

    public static void toast(String text){
        Toast.makeText(LibApplication.getContext(),text,Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String text){
        Toast.makeText(LibApplication.getContext(),text,Toast.LENGTH_LONG).show();
    }

    public static void toastOnUiThread(final String text){
        HandlerManager.getInstance().post(new Runnable() {
            @Override
            public void run() {
                toast(text);
            }
        });
    }

    public static void toastOnUiThreadLong(final String text){
        HandlerManager.getInstance().post(new Runnable() {
            @Override
            public void run() {
                toastLong(text);
            }
        });
    }
}
