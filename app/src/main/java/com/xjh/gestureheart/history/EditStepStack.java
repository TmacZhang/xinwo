package com.xjh.gestureheart.history;

import android.util.Log;

import java.util.LinkedList;

/**
 * Created by 25623 on 2018/3/24.
 *
 * Stack实际上LinkedList的装饰类
 */

public class EditStepStack {
    private final String TAG = "EditStepStack";

    private LinkedList<EditStepChain> mList;
    private static EditStepStack mStack = null;
    private EditStepChain mCurrentChain;
    private int mStepCount;

    private EditStepStack(){
        mList = new LinkedList<EditStepChain>();
        init();
    }

    /**
     * 初始化EditStepStack
     * Stack默认包含一条链，这条连中默认有一个从0到Long.MAX_VALUE的 filter0 (也即没有滤镜)
     */
    private void init() {
        EditStepDetail detail = new EditStepDetail(0, 0, Long.MAX_VALUE);
        mCurrentChain = new EditStepChain(detail);
        mList.add(mCurrentChain);
        mStepCount = 0;// 第0步是EditStepDetail(0, 0, Long.MAX_VALUE); 表示从视频开始到结束应用滤镜0，即没有应用滤镜
    }

    public static EditStepStack getInstance(){
        if(mStack == null){
            synchronized (EditStepStack.class){
                if(mStack == null){
                    mStack = new EditStepStack();
                }
            }
        }
        return mStack;
    }

    public void addDetail(EditStepDetail detail){
        Log.e(TAG,"addDetail: detail = " + detail.toString());

        EditStepChain chain = new EditStepChain(mCurrentChain, detail);

        mList.add(chain);
        mCurrentChain = chain;
        ++mStepCount;
    }

    /**
     * 撤销
     */
    public void retrieve(){
        //不能把第0步删除
        if(mStepCount > 0){
            mList.removeLast();
            mCurrentChain = mList.getLast();
            --mStepCount;
        }
    }

    /**
     * 获取timeStamp对应的滤镜
     * @param timeStamp
     * @return
     */
    public int getFilter(long timeStamp){
        return mCurrentChain.getFilterIndex(timeStamp);
    }

    public EditStepChain getLastEditStepChain(){
        if(mList != null  && mList.size() > 0){
            return mList.getLast();
        }
        return null;
    }

    public long getLastTimeStamp(){
        EditStepChain lastEditStepChain = getLastEditStepChain();
        if(lastEditStepChain == null){
            return 0;
        }

        int length = lastEditStepChain.details.length;
        if(lastEditStepChain.details[length -1].filterIndex != 0){
            //如果最后一段不是 空滤镜
            return lastEditStepChain.details[length -1].endTimestamp;
        }else{
            if(length > 1){
                return lastEditStepChain.details[length - 2].endTimestamp;
            }else{
                return 0;
            }
        }
    }

    public void clear(){
        mList.clear();
        init();
    }

    @Override
    public String toString() {
        return mCurrentChain.toString();
    }
}
