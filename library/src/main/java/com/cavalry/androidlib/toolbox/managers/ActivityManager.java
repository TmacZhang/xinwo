package com.cavalry.androidlib.toolbox.managers;

import android.app.Activity;

import java.util.LinkedList;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class ActivityManager {
    private static final String TAG = ActivityManager.class.getSimpleName();

    private static ActivityManager instance = null;
    private static LinkedList<Activity> mActivities = new LinkedList<Activity>();

    private ActivityManager() {

    }

    public static ActivityManager getInstance() {
        if (null == instance) {
            synchronized (ActivityManager.class) {
                if (null == instance) {
                    instance = new ActivityManager();
                }
            }
        }
        return instance;
    }

    public int size() {
        return mActivities.size();
    }

    /**
     * 获取顶部的Activity
     *
     * @return
     */
    public synchronized Activity getTopActivity() {
        return size() > 0 ? mActivities.get(size() - 1) : null;
    }

    public synchronized void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    public synchronized void removeActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            mActivities.remove(activity);
        }
    }

    public synchronized void removeActivity(Class clazz) {
        for (int size = mActivities.size(), i = size - 1; i >= 0; --i) {
            if (mActivities.get(i).getClass().getName().equals(clazz.getName())) {
                Activity activity = mActivities.get(i);
                removeActivity(activity);
                activity.finish();
            }
        }
    }

    /**
     * 清除所有Activity
     */
    public synchronized void clear() {
        for (int i = mActivities.size() - 1; i > -1; i--) {
            Activity activity = mActivities.get(i);
            removeActivity(activity);
            activity.finish();
            i = mActivities.size();
        }
    }

    /**
     * 只留Top Activity, 其余清除
     */
    public synchronized void clearToTop() {
        for (int i = mActivities.size() - 2; i > -1; i--) {
            Activity activity = mActivities.get(i);
            removeActivity(activity);
            activity.finish();
            i = mActivities.size() - 1;
        }
    }

    /**
     * 清除顶部的几个Activity
     *
     * @param number 清除的个数
     */
    public synchronized void removeAbove(int number) {
        int size = mActivities.size();
        if (number > size) {
            number = size;
        }

        for (int i = size - 1; i > size - 1 - number; --i) {
            Activity activity = mActivities.get(i);
            removeActivity(activity);
            activity.finish();
        }
    }

    /**
     * 清除clazz之上的Activity
     *
     * @param clazz
     */
    public synchronized void removeAbove(Class clazz) {
        for (int size = mActivities.size(), i = size - 1; i >= 0; --i) {
            if (!mActivities.get(i).getClass().getName().equals(clazz.getName())) {
                Activity activity = mActivities.get(i);
                removeActivity(activity);
                activity.finish();
            } else {
                break;
            }
        }
    }
}
