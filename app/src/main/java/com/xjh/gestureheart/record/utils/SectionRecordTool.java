package com.xjh.gestureheart.record.utils;

import android.util.SparseLongArray;

/**
 * Created by 25623 on 2018/8/1.
 */

public class SectionRecordTool {
    private static SectionRecordTool mTools;
    private SparseLongArray mSectionIndexArray;
    private int mSectionCount;
    private long mCurrentStartRecrodTimeMillis;
    private long mCurrentTotalRecordTimeMillis;

    private SectionRecordTool(){
        mSectionIndexArray = new SparseLongArray();
    }

    public static SectionRecordTool getInstance(){
        if(mTools == null){
            synchronized (SectionRecordTool.class){
                if(mTools == null){
                    mTools = new SectionRecordTool();
                }
            }
        }
        return mTools;
    }

    public void setCurrentStartRecrodTimeMillis(long timeMillis){
        mCurrentStartRecrodTimeMillis = timeMillis;
    }

    public long getCurrentStartRecrodTimeMillis(){
        return mCurrentStartRecrodTimeMillis;
    }

    public void setCurrentTotalRecordTimeMillis(long timeMillis){
        mCurrentTotalRecordTimeMillis = timeMillis;
    }

    public long getCurrentTotalRecordTimeMillis(){
        return mCurrentTotalRecordTimeMillis;
    }

    public void addSectionIndex(long timeMillis){
        mSectionIndexArray.put(mSectionCount++, timeMillis);
    }

    public long getLastSectionIndex(){
        if(mSectionIndexArray.size() > 0){
            return mSectionIndexArray.get(mSectionCount-1);
        }else{
            return 0;
        }
    }

    public void removeCurrentSectionIndex(){
        if(mSectionCount > 0){
            mSectionIndexArray.removeAt(--mSectionCount);
            if(mSectionCount > 0){
                mCurrentTotalRecordTimeMillis = mSectionIndexArray.get(mSectionCount-1);
            }else{
                mCurrentTotalRecordTimeMillis = 0;
            }

        }
    }

    public void reset(){
        mSectionIndexArray.clear();
        mSectionCount = 0;
        mCurrentStartRecrodTimeMillis = 0;
        mCurrentTotalRecordTimeMillis = 0;
    }

}
