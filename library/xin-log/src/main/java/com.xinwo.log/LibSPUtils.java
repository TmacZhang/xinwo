package com.xinwo.log;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class LibSPUtils {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void put(String key, Object object) {
        put(FILE_NAME, key, object);
    }

    public static void put(String spName, String key, Object object) {
        SharedPreferences sp = getContext().getSharedPreferences(spName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }


    public static String getString(String key, String defaultValue) {
        SharedPreferences sp = getContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        return sp.getString(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        SharedPreferences sp = getContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        return sp.getInt(key, defaultValue);
    }


    public static float getFloat(String key, float defaultValue) {
        SharedPreferences sp = getContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        return sp.getFloat(key, defaultValue);
    }


    public static long getLong(String key, long defaultValue) {
        SharedPreferences sp = getContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        return sp.getLong(key, defaultValue);
    }


    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sp = getContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        return sp.getBoolean(key, defaultValue);
    }


    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(String key) {
        remove(FILE_NAME, key);
    }

    public static void remove(String spName, String key) {
        SharedPreferences sp = getContext().getSharedPreferences(spName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        clear(FILE_NAME);
    }

    public static void clear(String spName) {
        SharedPreferences sp = getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     */
    public static boolean contains(String key) {
        return contains(FILE_NAME, key);
    }

    public static boolean contains(String spName, String key) {
        SharedPreferences sp = getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll() {
        return getAll(FILE_NAME);
    }

    public static Map<String, ?> getAll(String spName) {
        SharedPreferences sp = getContext().getSharedPreferences(spName,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException ignored) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
            }
            editor.commit();
        }
    }

    public static Context getContext() {
        return XinApplicationUtil.Companion.getInstance().getMApplication();
    }
}