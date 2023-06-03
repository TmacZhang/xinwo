package com.xinwo.log;

import android.view.View;

import org.jetbrains.annotations.Nullable;

public class LibUtils {
    /**
     * 检查某个对象是否为空
     *
     * @param reference
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static void printMeasuredDimen(String tag, View view) {
        LibLog.e("DIMEN - " + tag, "width = "
                + view.getMeasuredWidth() + "      height = " + view.getMeasuredHeight());
    }
}
