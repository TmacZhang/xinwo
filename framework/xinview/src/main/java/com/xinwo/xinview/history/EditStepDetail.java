package com.xinwo.xinview.history;

/**
 * Created by 25623 on 2018/3/23.
 * 编辑的每一步的具体具体内容
 */

public  class EditStepDetail {
    public int filterIndex;
    public long startTimestamp;
    public long endTimestamp;   // Long.Max 表示视频结尾

    public EditStepDetail(){}

    public EditStepDetail(int filterIndex, long startTimestamp, long endTimestamp){
        this.filterIndex = filterIndex;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    @Override
    public String toString() {
        return "("+filterIndex+", "+startTimestamp+", "+endTimestamp+")";
    }
}
