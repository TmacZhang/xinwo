package com.xinwo.produce.gestureheart.mediacodec;

/**
 * Created by 25623 on 2018/2/2.
 *
 * 可避免无效的wait()/notify()的锁
 */

public class SingleLockObject {
    private Object mLock = new Object();

    private boolean mIsWaiting = false;

    public SingleLockObject(){

    }

    public void await(){
        if(mIsWaiting)
            return;

        synchronized (mLock){
            try {
                mIsWaiting = true;
                mLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void signal(){
        if(!mIsWaiting)
            return;

        synchronized (mLock){
            mLock.notify();
            mIsWaiting = false;
        }
    }

}
