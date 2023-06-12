package com.xinwo.produce.gestureheart.mediacodec;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;


import com.xinwo.xinview.history.HistoryConstans;
import com.xinwo.xinview.history.ShakeStack;

import java.nio.ByteBuffer;

/**
 * Created by 25623 on 2018/2/1.
 */

public class VideoDecodeRunnable implements Runnable {
    private final String TAG = "VideoDecodeRunnable";
    private final int TIMEOUT_USEC = 10000;

    private CavalryMediaPlayer mMediaPlayer;
    private boolean mDecodingQueueInputBuffer = false;
    private boolean mDecodingReleaseOutputBuffer = false;


    private SingleLockObject mDecodeLock = new SingleLockObject();
    private boolean mInitStart;
    private boolean inputDone;
    private boolean outputDone;
    private boolean mSeeking;

    private long mLastDecodeTime = -1;
    private long mDecodeSleepTime = 0;
    private long mCurrentDeocdeTime;



    private int mCurrentShakeTimes = 0;
    private int mCurrentShakeStep = 0;
    private boolean mShake;
    private long mCurrentShakePointTimestamp;


    public VideoDecodeRunnable(CavalryMediaPlayer mediaPlayer){
        mInitStart = true;
        mMediaPlayer = mediaPlayer;
    }

    public void startDecode(){
        mLastDecodeTime = -1;
        mInitStart = false;
        mDecodingQueueInputBuffer = true;

        inputDone = false;
        outputDone = false;

        if(HistoryConstans.mReverse){
            mMediaPlayer.videoExtractor.seekTo(mMediaPlayer.getVideoDurationTimestamp(), MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }

        mDecodeLock.signal();

        mMediaPlayer.onDecodeStart();
        mMediaPlayer.setPlayState(CavalryMediaPlayer.PLAY_STATE_STARTED);

    }

    public void pauseDecode(){
        mDecodingQueueInputBuffer = false;
    }


    @Override
    public void run() {
        decode();
    }

    private void decode() {
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        ByteBuffer[] decoderInputBuffers = mMediaPlayer.videoDecoder.getInputBuffers();


        int inputBufferIndex = -1;
        int outputBufferIndex = -1;

        inputDone = false;
        outputDone = false;
        boolean doRender = false;

        long queueInputBufferCount = 0;
        long releaseOutputBufferCount = 0;

        while(true){
            if(!mDecodingQueueInputBuffer && outputDone){
                /**
                 *  用户点击暂停之后（也即 mDecodingQueueInputBuffer = false）, 有部分已extract的数据未decode,
                 *  所以要等到outputDone = true时，才算真正pause
                 * **/
                /** 用户主动暂停 **/
                mMediaPlayer.setPlayState(CavalryMediaPlayer.PLAY_STATE_PAUSED);
                if(!mInitStart){
                    mMediaPlayer.onDecodePause();
                }
                Log.e(TAG,"用户主动暂停触发await---------");
                mDecodeLock.await();
            }

            if(outputDone){
                mMediaPlayer.setPlayState(CavalryMediaPlayer.PLAY_STATE_COMPLETE);
                mMediaPlayer.onDecodeComplete();
                Log.e(TAG,"播放到文件结尾触发await---------");
                mMediaPlayer.videoDecoder.flush();// reset decoder state
                mDecodeLock.await();
            }

            //1.从文件读取数据
            if(mDecodingQueueInputBuffer && !inputDone){
                inputBufferIndex = mMediaPlayer.videoDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                if(inputBufferIndex >= 0){
                    ByteBuffer inputBuffer = decoderInputBuffers[inputBufferIndex];
                    inputBuffer.clear();

                    long presentationTimeUS = mMediaPlayer.videoExtractor.getSampleTime();
                    Log.i(TAG,"SYNC sample Video = " + mMediaPlayer.audioExtractor.getSampleTime());
                    Log.i(TAG,"seekTo presentationTimeUS001 = " + presentationTimeUS);
                    int chuckSize = mMediaPlayer.videoExtractor.readSampleData(inputBuffer, 0);


                    if(chuckSize > 0){
//                        presentationTimeUS = mMediaPlayer.videoExtractor.getSampleTime();
                        Log.i(TAG,"seekTo presentationTimeUS002 = " + presentationTimeUS);
                        mMediaPlayer.videoDecoder.queueInputBuffer(inputBufferIndex, 0, chuckSize,
                                presentationTimeUS, 0);

                        if(ShakeStack.getInstance().isShakeTimestamp(presentationTimeUS) && mCurrentShakeTimes < ShakeStack.SHAKE_TIMES){
                            mCurrentShakePointTimestamp = presentationTimeUS;
                            mShake = true;
                        }

                        if(mShake){
                            if(mCurrentShakeStep < ShakeStack.VIDEO_SHAKE_STEP_LENGTH){
                                ++mCurrentShakeStep;
                                Log.i(TAG,"shake mCurrentShakeStep = " + mCurrentShakeStep);

                            }else{
                                mMediaPlayer.videoExtractor.seekTo(mCurrentShakePointTimestamp, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                                //Audio的时间戳比Video多，所以由Video这里控制Seek
                                mMediaPlayer.audioExtractor.seekTo(mCurrentShakePointTimestamp, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

                                ++mCurrentShakeTimes;
                                mCurrentShakeStep = 0;

                                presentationTimeUS = mCurrentShakePointTimestamp;
                                Log.e(TAG,"shake presentationTimeUS = " + presentationTimeUS + " mCurrentShakePointTimestamp = " + mCurrentShakePointTimestamp + "    newTimeStamp = " + mMediaPlayer.videoExtractor.getSampleTime());

                                if(mCurrentShakeTimes == ShakeStack.SHAKE_TIMES){
                                    mShake = false;
                                    mCurrentShakeTimes = 0;
                                    mCurrentShakeStep = 0;
                                }
                            }
                        }

                        if(HistoryConstans.mReverse){
                            mMediaPlayer.videoExtractor.seekTo(presentationTimeUS - 33000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                            Log.i(TAG,"seekTo presentationTimeUS003 = " + presentationTimeUS);
                        }else{
                            mMediaPlayer.videoExtractor.advance();//转到下一帧
                            Log.i(TAG,"seekTo presentationTimeUS003 = " + presentationTimeUS);
                        }

                        ++queueInputBufferCount;
                        mMediaPlayer.putVideoTimestampSequent(presentationTimeUS);
                        Log.e(TAG,"seekTo presentationTimeUS004 = " + presentationTimeUS);

                        if(mSeeking){
                            Log.i(TAG,"readerSampleData mSeeking = " + mSeeking);
                            mDecodingQueueInputBuffer = false;
                        }
                        if(HistoryConstans.mReverse){
                            if(presentationTimeUS == 0){
                                Log.e(TAG,"seekTo presentationTimeUS005 = " + presentationTimeUS);
                                inputDone = true;
                            }
                        }

                    }else{
                        Log.e(TAG,"timestamp00 = 读取到文件结尾");
                        //读取到文件结尾，为了重复使用，不能有BUFFER_FLAG_END_OF_STREAM
//                        mMediaPlayer.videoDecoder.queueInputBuffer(inputBufferIndex, 0, 0, 0L,
//                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    }
                }else{
                    Log.d(TAG,"input buffer not available");
                }
            }//end of 从文件读取数据

            //2.将读取的数据解码
            if(!outputDone){
                outputBufferIndex = mMediaPlayer.videoDecoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                Log.i(TAG," VIDEOPT  presentationTimeUS006 ======== outputBufferIndex = " + outputBufferIndex + "   mMediaPlayer.videoExtractor.getSampleTime() = " + mMediaPlayer.videoExtractor.getSampleTime());
                switch (outputBufferIndex){
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        //TODO 视频卡顿的原因是 output循环没有执行，最后导致inputDone但这里没有done
                        if(mSeeking)
                            break;

                        if(inputDone){//文件主动播放结束
                            outputDone = true;
                            if(HistoryConstans.mReverse){
                                //倒序播放的时候，timestamp = 0 的数据发送不过去，导致进度调不能回到0
                                mMediaPlayer.putVideoTimestampSequent(0);
                                Log.i(TAG," VIDEOPT  presentationTimeUS007 ======== 新加一个000000 ");
                            }
                        }else if(!mDecodingQueueInputBuffer){//用户手动暂停
                            outputDone = true;
                        }
                        Log.d(TAG, "INFO_TRY_AGAIN_LATER --> no output from decoder available");
                        break;
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED --> decoder output buffers changed");
                        break;
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        MediaFormat newFormat = mMediaPlayer.videoDecoder.getOutputFormat();
                        Log.d(TAG,"INFO_OUTPUT_FORMAT_CHANGED --> decoder output format changed");
                        break;
                    default:
                        if(outputBufferIndex > 0){

                            //换到了 MediaCodec.INFO_TRY_AGAIN_LATER: 中
//                            if((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0){//文件结尾
//                                //TODO 读取到文件结尾
//                                outputDone = true;
//                                if(mMediaPlayer.mMediaCallback != null){
//                                    mMediaPlayer.mMediaCallback.onDecodeComplete();
//                                }
//                                mMediaPlayer.setPlayState(CavalryMediaPlayer.PLAY_STATE_COMPLETE);
//                            }

                            if(!mSeeking){
                                if(mLastDecodeTime == -1){
                                    mDecodeSleepTime = 0;
                                }else{
                                    mCurrentDeocdeTime = System.currentTimeMillis();

                                    mDecodeSleepTime = mMediaPlayer.frameInterval - (mCurrentDeocdeTime - mLastDecodeTime);

                                    Log.e(TAG,"mDecodeSleepTime = " + mMediaPlayer.frameInterval + " + " + mLastDecodeTime + "-" + mCurrentDeocdeTime + " = " + mDecodeSleepTime);

                                    if(mDecodeSleepTime < 0){
                                        mDecodeSleepTime = 0;
                                    }
                                }

                                //按帧率休眠
                                try{
                                    Thread.sleep(mDecodeSleepTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }



//                            mMediaPlayer.videoPTimeQueue.add(bufferInfo.presentationTimeUs);

                            doRender = (bufferInfo.size != 0);//不是文件结尾才需要渲染
                            Log.i(TAG," VIDEOPT  presentationTimeUS005 ======== = " + bufferInfo.presentationTimeUs + " bufferInfo.size = " + bufferInfo.size);
                            mMediaPlayer.videoDecoder.releaseOutputBuffer(outputBufferIndex, doRender);//数据发送到Surface

                            mLastDecodeTime = System.currentTimeMillis();

                            if(mSeeking){
                                mSeeking = false;
                                Log.i(TAG,"releaseOutputBuffer mSeeking = " + mSeeking);
                                outputDone = true;
                                pauseDecode();
                            }

                            if(doRender){
                                ++releaseOutputBufferCount;
                            }

                            Log.e(TAG,"1738 --> releaseOutputBufferCount = " + releaseOutputBufferCount);

                        }else{
                            throw new RuntimeException(
                                    "unexpected result from decoder.dequeueOutputBuffer: " +
                                            outputBufferIndex);
                        }
                        break;
                }
            }//end of 数据解码
        }// end of while
    }



    public void seekTo(long time) {
        Log.i(TAG,"seekTo time = " + time);
        mMediaPlayer.videoExtractor.seekTo(time, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        if(!mMediaPlayer.isPlaying()){//如果没有播放
            mSeeking = true;

            mLastDecodeTime = -1;
            mInitStart = false;
            mDecodingQueueInputBuffer = true;

            inputDone = false;
            outputDone = false;

            mDecodeLock.signal();
        }
    }

    public void reverse(){
        HistoryConstans.mReverse = true;
        startDecode();
    }


    public void cancelReverse() {
        HistoryConstans.mReverse = false;
        pauseDecode();
        mMediaPlayer.videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
    }

}
