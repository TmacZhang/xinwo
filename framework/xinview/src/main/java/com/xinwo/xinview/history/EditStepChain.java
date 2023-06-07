package com.xinwo.xinview.history;

import android.util.Log;

/**
 * Created by 25623 on 2018/3/23.
 * 编辑的步骤
 */

public class EditStepChain {
    private final String TAG = "EditStepChain";

    public int totalDetailCount;
    public EditStepDetail[] details;


    public EditStepChain(EditStepDetail detail){
        totalDetailCount = 1;
        details = new EditStepDetail[totalDetailCount];
        details[0] = detail;
    }

    public EditStepChain(EditStepChain stepChain, EditStepDetail detail){

        int startTimestampIndex = whichDetailContains(stepChain, detail.startTimestamp);
        int endTimestampIndex = whichDetailContains(stepChain, detail.endTimestamp);

        Log.i(TAG,"step00 =========== startTimestampIndex = " + startTimestampIndex + "    endTimestampIndex = " + endTimestampIndex
        +"  detail.startTimestamp = " + detail.startTimestamp + "    detail.endTimestamp = " + detail.endTimestamp);

        if(startTimestampIndex == endTimestampIndex){ /**新的detail被包含在旧的detail中**/
            if(detail.filterIndex == stepChain.details[startTimestampIndex].filterIndex){
                Log.i(TAG," step01 oldDetailCount = " + stepChain.totalDetailCount);

                for(int i = 0; i<stepChain.totalDetailCount; i++){
                    Log.i(TAG,"step01 stepChain.details[" + i + "] = " + stepChain.details[i]);
                }

                //如果新的detail与旧的detail滤镜相同
                totalDetailCount = stepChain.totalDetailCount;
                details = new EditStepDetail[totalDetailCount];
                copyDetails(details, 0, stepChain.details, 0, stepChain.totalDetailCount);

                Log.e(TAG," step01 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                for(int i = 0; i<totalDetailCount; i++){
                    Log.e(TAG,"step01 details[" + i + "] = " + details[i]);
                }

            }else{//如果新的detail与旧的detail滤镜不不不同

                if((detail.startTimestamp == stepChain.details[startTimestampIndex].startTimestamp)
                        && (detail.endTimestamp == stepChain.details[startTimestampIndex].endTimestamp)){
                    //如果新的detail与旧的detail的startTimestamp,endTimestamp都相同
                    Log.i(TAG," step02 oldDetailCount = " + stepChain.totalDetailCount);
                    for(int i = 0; i<stepChain.totalDetailCount; i++){
                        Log.i(TAG,"step02 stepChain.details[" + i + "] = " + stepChain.details[i]);
                    }

                    totalDetailCount = stepChain.totalDetailCount;
                    details = new EditStepDetail[totalDetailCount];
                    copyDetails(details, 0, stepChain.details, 0, stepChain.totalDetailCount);
                    details[startTimestampIndex].filterIndex = detail.filterIndex;

                    Log.e(TAG," step02 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                    for(int i = 0; i<totalDetailCount; i++){
                        Log.e(TAG,"step02 details[" + i + "] = " + details[i]);
                    }

                }else if(detail.startTimestamp == stepChain.details[startTimestampIndex].startTimestamp){
                    Log.i(TAG," step03 oldDetailCount = " + stepChain.totalDetailCount);
                    for(int i = 0; i<stepChain.totalDetailCount; i++){
                        Log.i(TAG,"step03 stepChain.details[" + i + "] = " + stepChain.details[i]);
                    }

                    //如果新的detail与旧的detail仅startTimestamp相同
                    totalDetailCount = stepChain.totalDetailCount + 1;
                    details = new EditStepDetail[totalDetailCount];

                    copyDetails(details, 0, stepChain.details, 0, startTimestampIndex);

                    details[startTimestampIndex] = detail;
                    EditStepDetail detailOne = new EditStepDetail();
                    detailOne.filterIndex = stepChain.details[startTimestampIndex].filterIndex;
                    detailOne.startTimestamp = detail.endTimestamp;
                    detailOne.endTimestamp = stepChain.details[startTimestampIndex].endTimestamp;
                    details[startTimestampIndex + 1] = detailOne;
                    copyDetails(details, startTimestampIndex + 2, stepChain.details, startTimestampIndex + 1, stepChain.totalDetailCount - startTimestampIndex -1);

                    Log.e(TAG," step03 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                    for(int i = 0; i<totalDetailCount; i++){
                        Log.e(TAG,"step03 details[" + i + "] = " + details[i]);
                    }
                }else if(detail.endTimestamp == stepChain.details[startTimestampIndex].endTimestamp){
                    Log.i(TAG," step04 oldDetailCount = " + stepChain.totalDetailCount);
                    for(int i = 0; i<stepChain.totalDetailCount; i++){
                        Log.i(TAG,"step04 stepChain.details[" + i + "] = " + stepChain.details[i]);
                    }

                    //如果新的detail与旧的detail仅endTimestamp相同
                    totalDetailCount = stepChain.totalDetailCount + 1;
                    details = new EditStepDetail[totalDetailCount];

                    copyDetails(details, 0, stepChain.details, 0, startTimestampIndex);

                    EditStepDetail detailOne = new EditStepDetail();
                    detailOne.filterIndex = stepChain.details[startTimestampIndex].filterIndex;
                    detailOne.startTimestamp = detail.startTimestamp;
                    detailOne.endTimestamp = detail.endTimestamp;
                    details[startTimestampIndex] = detailOne;
                    details[startTimestampIndex + 1] = detail;
                    copyDetails(details, startTimestampIndex + 2, stepChain.details, startTimestampIndex + 1, stepChain.totalDetailCount  - startTimestampIndex -1);

                    Log.e(TAG," step04 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                    for(int i = 0; i<totalDetailCount; i++){
                        Log.e(TAG,"step04 details[" + i + "] = " + details[i]);
                    }
                }else{
                    Log.i(TAG," step05 oldDetailCount = " + stepChain.totalDetailCount);
                    for(int i = 0; i<stepChain.totalDetailCount; i++){
                        Log.i(TAG,"step05 stepChain.details[" + i + "] = " + stepChain.details[i]);
                    }

                    //新的detail的start,end 完全包含在旧的detail中
                    totalDetailCount = stepChain.totalDetailCount + 2;
                    details = new EditStepDetail[totalDetailCount];

                    copyDetails(details, 0, stepChain.details, 0, startTimestampIndex);

                    EditStepDetail detailOne = new EditStepDetail();
                    detailOne.filterIndex = stepChain.details[startTimestampIndex].filterIndex;
                    detailOne.startTimestamp = stepChain.details[startTimestampIndex].startTimestamp;
                    detailOne.endTimestamp = detail.startTimestamp;
                    details[startTimestampIndex] = detailOne;
                    details[startTimestampIndex + 1] = detail;
                    EditStepDetail detailThree = new EditStepDetail();
                    detailThree.filterIndex = stepChain.details[startTimestampIndex].filterIndex;
                    detailThree.startTimestamp = detail.endTimestamp;
                    detailThree.endTimestamp = stepChain.details[startTimestampIndex].endTimestamp;
                    details[startTimestampIndex + 2] = detailThree;

                    copyDetails(details,startTimestampIndex + 3, stepChain.details, startTimestampIndex + 1, stepChain.totalDetailCount - startTimestampIndex -1);

                    Log.e(TAG," step05 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                    for(int i = 0; i<totalDetailCount; i++){
                        Log.e(TAG,"step05 details[" + i + "] = " + details[i]);
                    }
                }
            }
        }else{/**新的detail跨多个旧的detail**/
            if((detail.startTimestamp == stepChain.details[startTimestampIndex].startTimestamp)
                    && (detail.endTimestamp == stepChain.details[endTimestampIndex].endTimestamp)){
                Log.i(TAG," step06 oldDetailCount = " + stepChain.totalDetailCount);
                for(int i = 0; i<stepChain.totalDetailCount; i++){
                    Log.i(TAG,"step06 stepChain.details[" + i + "] = " + stepChain.details[i]);
                }

                //新detail完整的跨过多个旧detail
                totalDetailCount = stepChain.totalDetailCount + 1 - (endTimestampIndex - startTimestampIndex +1);
                details = new EditStepDetail[totalDetailCount];

                copyDetails(details, 0, stepChain.details, 0, startTimestampIndex);
                details[startTimestampIndex] = detail;
                copyDetails(details, startTimestampIndex + 1, stepChain.details, endTimestampIndex + 1, stepChain.totalDetailCount - endTimestampIndex -1);

                Log.e(TAG," step06 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                for(int i = 0; i<totalDetailCount; i++){
                    Log.e(TAG,"step06 details[" + i + "] = " + details[i]);
                }
            }else if(detail.startTimestamp == stepChain.details[startTimestampIndex].startTimestamp){
                Log.i(TAG," step07 oldDetailCount = " + stepChain.totalDetailCount);
                for(int i = 0; i<stepChain.totalDetailCount; i++){
                    Log.i(TAG,"step07 stepChain.details[" + i + "] = " + stepChain.details[i]);
                }

                //仅startTimestamp相同
                totalDetailCount = stepChain.totalDetailCount + 1 - (endTimestampIndex - startTimestampIndex);
                details = new EditStepDetail[totalDetailCount];

                copyDetails(details, 0, stepChain.details, 0, startTimestampIndex);
                details[startTimestampIndex] = detail;
                EditStepDetail detailTwo = new EditStepDetail();
                detailTwo.filterIndex = stepChain.details[startTimestampIndex].filterIndex;
                detailTwo.startTimestamp = detail.endTimestamp;
                detailTwo.endTimestamp = stepChain.details[endTimestampIndex].endTimestamp;
                details[startTimestampIndex + 1] = detailTwo;

                copyDetails(details, startTimestampIndex + 2, stepChain.details, endTimestampIndex + 1, stepChain.totalDetailCount - endTimestampIndex - 1);

                Log.e(TAG," step07 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                for(int i = 0; i<totalDetailCount; i++){
                    Log.e(TAG,"step07 details[" + i + "] = " + details[i]);
                }
            }else if(detail.endTimestamp == stepChain.details[endTimestampIndex].endTimestamp){
                Log.i(TAG," step08 oldDetailCount = " + stepChain.totalDetailCount);
                for(int i = 0; i<stepChain.totalDetailCount; i++){
                    Log.i(TAG,"step08 stepChain.details[" + i + "] = " + stepChain.details[i]);
                }

                //仅endTimestamp相同
                totalDetailCount = stepChain.totalDetailCount + 1 - (endTimestampIndex - startTimestampIndex);
                details = new EditStepDetail[totalDetailCount];

                copyDetails(details, 0, stepChain.details, 0, startTimestampIndex);
                EditStepDetail detailOne = new EditStepDetail();
                detailOne.filterIndex = stepChain.details[startTimestampIndex].filterIndex;
                detailOne.startTimestamp = stepChain.details[startTimestampIndex].startTimestamp;
                detailOne.endTimestamp = detail.startTimestamp;
                details[startTimestampIndex] = detailOne;
                details[startTimestampIndex + 1] = detail;

                copyDetails(details, startTimestampIndex + 2, stepChain.details, endTimestampIndex + 1, stepChain.totalDetailCount - endTimestampIndex -1);

                Log.e(TAG," step08 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                for(int i = 0; i<totalDetailCount; i++){
                    Log.e(TAG,"step08 details[" + i + "] = " + details[i]);
                }
            }else{
                //startTimestamp 与 endTimestamp 都不相同
                Log.i(TAG," step09 oldDetailCount = " + stepChain.totalDetailCount);
                for(int i = 0; i<stepChain.totalDetailCount; i++){
                    Log.i(TAG,"step09 stepChain.details[" + i + "] = " + stepChain.details[i]);
                }

                totalDetailCount = stepChain.totalDetailCount + 1 - (endTimestampIndex - startTimestampIndex -1);
                details = new EditStepDetail[totalDetailCount];

                copyDetails(details, 0, stepChain.details, 0, startTimestampIndex);
                EditStepDetail detailOne = new EditStepDetail();
                detailOne.filterIndex = stepChain.details[startTimestampIndex].filterIndex;
                detailOne.startTimestamp = stepChain.details[startTimestampIndex].startTimestamp;
                detailOne.endTimestamp = detail.startTimestamp;
                details[startTimestampIndex] = detailOne;

                details[startTimestampIndex + 1] = detail;

                EditStepDetail detailThree = new EditStepDetail();
                detailThree.filterIndex = stepChain.details[endTimestampIndex].filterIndex;
                detailThree.startTimestamp = detail.startTimestamp;
                detailThree.endTimestamp = stepChain.details[endTimestampIndex].endTimestamp;
                details[startTimestampIndex + 2] = detailThree;

                copyDetails(details, startTimestampIndex + 3, stepChain.details, endTimestampIndex + 1, stepChain.totalDetailCount - endTimestampIndex -1);

                Log.e(TAG," step09 totalDetailCount = " + totalDetailCount + "   details.length = " + details.length);
                for(int i = 0; i<totalDetailCount; i++){
                    Log.e(TAG,"step09 details[" + i + "] = " + details[i]);
                }
            }
        }

        Log.i(TAG,detail.toString() + " =====================================");
    }

    /**
     * 获取timeStamp对应的滤镜
     * @param timeStamp
     * @return
     */
    public int getFilterIndex(long timeStamp){
        int detailIndex = whichDetailContains(this, timeStamp);
        return details[detailIndex].filterIndex;
    }

    private void copyDetails(EditStepDetail[] destDetails, int destStart, EditStepDetail[] srcDetails, int srcStart, int length) {
        for(int i = 0; i<length; ++i){
            destDetails[destStart] = new EditStepDetail();
            destDetails[destStart].filterIndex = srcDetails[srcStart].filterIndex;
            destDetails[destStart].startTimestamp = srcDetails[srcStart].startTimestamp;
            destDetails[destStart].endTimestamp = srcDetails[srcStart].endTimestamp;
            ++srcStart;
            ++destStart;
        }
    }

    /**
     * 那个detail包含timestamp
     * @param editStep
     * @param timestamp
     * @return
     */
    private int whichDetailContains(EditStepChain editStep, long timestamp){
        int oldUsedDetailCount = editStep.totalDetailCount;
        int i = 0;


        while(i < oldUsedDetailCount){
            if(isContains(editStep.details[i], timestamp)){
          //      Log.e(TAG,"whichDetailContains  editStep.details["+i+"] = " + editStep.details[i]);
                break;
            }
            ++i;
        }

        if(timestamp == -1){// -1表示播放到了文件的结尾。-1并不包含在上一步的editStep中，所以会导致while中i多加1
            --i;
        }

        return i;
    }

    /**
     * 指定的detail是否包含timeStamp
     * @param detail
     * @param timestamp
     * @return
     */
    private boolean isContains(EditStepDetail detail ,long timestamp){
        if((timestamp >= detail.startTimestamp) && (timestamp < detail.endTimestamp)){//[startTimestamp, endTimestamp)
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<totalDetailCount; i++){
            sb.append("["+i+"]")
                    .append(details[i].toString())
                    .append("   ");
        }
        return sb.toString();
    }
}
