package com.xinwo.log;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;


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

    public static int getColor(Context context, int id) {
        return context.getResources().getColor(id);
    }

    public static String getString(int id) {
        return XinApplicationUtil.Companion.getInstance().getMApplication().getResources().getString(id);
    }

    public static Drawable getDrawable(Context context, int id) {
        return context.getResources().getDrawable(id);
    }
}
