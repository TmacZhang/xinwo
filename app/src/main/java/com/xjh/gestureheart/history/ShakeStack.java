package com.xjh.gestureheart.history;

/**
 * Created by 25623 on 2018/5/4.
 * 鬼畜历史
 */

public class ShakeStack {
    private static ShakeStack mShakeStack;
    private long mShakeTimestamp = -1;
    public static  final int SHAKE_TIMES = 3;
    public static int VIDEO_SHAKE_STEP_LENGTH = 10;

    private ShakeStack(){}

    public static ShakeStack getInstance(){
        if(mShakeStack == null){
            synchronized (ShakeStack.class){
                if(mShakeStack == null){
                    mShakeStack = new ShakeStack();
                }
            }
        }

        return mShakeStack;
    }

    public void setShakeTimestamp(long timestamp){
        mShakeTimestamp = timestamp;
    }

    public void removeShakeTimestamp(){
        mShakeTimestamp = -1;
    }

    public boolean isShakeTimestamp(long timestamp){
        return mShakeTimestamp == timestamp;
    }

    public boolean hasShakeTimestamp(){
        return mShakeTimestamp > 0;
    }

    public long getShakeTimestamp(){
        return mShakeTimestamp;
    }
}
