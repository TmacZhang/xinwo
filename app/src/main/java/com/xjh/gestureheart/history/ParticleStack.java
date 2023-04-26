package com.xjh.gestureheart.history;

import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseLongArray;

/**
 * Created by 25623 on 2018/4/1.
 */

public class ParticleStack {
    private final static String TAG = "ParticleStack";
    /**
     * LongSparseArray<SparseArray<ParticlePoint>> 中的LongSparseArray<>是按照timestamp顺序存储
     * SparseArray<ParticlePoint> 是按照step(编辑步骤)的顺序存储的ParticlePoint
     **/
    private LongSparseArray<SparseArray<ParticlePoint>> mTimeList;
    private static ParticleStack mStack = null;
    private int mCurrentStep;
    private SparseLongArray mParticleStartTimestampList;
    private long mRetrievedParticleStartTimestamp;  //最近一次删除的粒子step的开始 particlePointShowTimestamp

    private ParticleStack(){
        mTimeList = new LongSparseArray<SparseArray<ParticlePoint>>();
        mCurrentStep = -1;
        mParticleStartTimestampList = new SparseLongArray();
    }

    public static ParticleStack getInstance(){
        if(mStack == null){
            synchronized (ParticleStack.class){
                if(mStack == null){
                    mStack = new ParticleStack();
                }
            }
        }

        return mStack;
    }

    public void addPoint(ParticlePoint point){
        SparseArray<ParticlePoint> pointArray = mTimeList.get(point.particlePointShowTimestamp);
        if(pointArray == null){
            pointArray = new SparseArray<ParticlePoint>();
            mTimeList.put(point.particlePointShowTimestamp, pointArray);
        }
        pointArray.put(point.step, point);
        mCurrentStep = point.step;
    }

    public SparseArray<ParticlePoint> getPoints(long timestamp){
        return mTimeList.get(timestamp);
    }

    public SparseArray<ParticlePoint> getPointsAt(int index){
        return mTimeList.valueAt(index);
    }

    public int indexOf(long timestamp){
        return mTimeList.indexOfKey(timestamp);
    }

    /**
     * 获取当前步骤
     * @return
     */
    public int getCurrentStep(){
        return mCurrentStep;
    }

    /**
     * 撤销
     */
    public void retrieve(){
        if(mCurrentStep < 0)
            return;

        int size = mTimeList.size();
        SparseArray<ParticlePoint> pointArray = null;
        for(int i = 0; i < size; ++i){
            pointArray = mTimeList.valueAt(i);
            Log.e(TAG,"pointArray" + i + ":  " + pointArray.toString());
            pointArray.remove(mCurrentStep);
            Log.i(TAG,"pointArray" + i + ":  " + pointArray.toString() + "  =========");
        }

        mRetrievedParticleStartTimestamp = mParticleStartTimestampList.get(mCurrentStep);
        mParticleStartTimestampList.removeAt(mCurrentStep);
        --mCurrentStep;
    }


    public void clear(){
        int size = mTimeList.size();
        SparseArray<ParticlePoint> pointArray = null;
        for(int i = 0; i < size; ++i){
            pointArray = mTimeList.valueAt(i);
            pointArray.clear();
        }
        mTimeList.clear();
        mParticleStartTimestampList.clear();
        mCurrentStep = -1;
    }

    /**
     * 存储粒子的第一个point的timestamp
     * @param currentTimestamp
     */
    public void putParticleStartTimestamp(int step, long currentTimestamp) {
        mParticleStartTimestampList.put(step, currentTimestamp);
    }

    /**
     * 获取最近一次删除的粒子step的开始 particlePointShowTimestamp
     * @return
     */
    public long getRetrievedParticleStartTimestamp(){
        return mRetrievedParticleStartTimestamp;
    }

}

