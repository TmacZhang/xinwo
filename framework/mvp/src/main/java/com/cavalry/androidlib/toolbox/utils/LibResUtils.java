package com.cavalry.androidlib.toolbox.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import com.cavalry.androidlib.LibApplication;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class LibResUtils {

    public static LayoutInflater getLayoutInflater(Context context) {
        return LayoutInflater.from(context);
    }

    public static View getInflaterView(Context context, int resource) {
        LayoutInflater inflater = getLayoutInflater(context);
        return inflater.inflate(resource, null);
    }

    public static int getColor(int id) {
        return LibApplication.getContext().getResources().getColor(id);
    }

    public static String getString(int id) {
        return LibApplication.getContext().getResources().getString(id);
    }

    public static Drawable getDrawable(int id) {
        return LibApplication.getContext().getResources().getDrawable(id);
    }
}
