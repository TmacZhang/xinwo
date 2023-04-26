package com.xjh.gestureheart.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.xjh.gestureheart.history.HistoryConstans;
import com.xjh.gestureheart.history.ShakeStack;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.media.MediaExtractor.SEEK_TO_PREVIOUS_SYNC;

/**
 * Created by Administrator on 2017/6/19 0019.
 * desc：用于视频裁剪的类
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class VideoClipper {
    private final String TAG = "VideoClipper";
    private final int AUDIO_SAMPLE_RATE = 44100;
    private final int AUDIO_CHANNEL_COUNT = 2;

    final int TIMEOUT_USEC = 0;
    private String mInputVideoPath;
    private String mInputAudioPath;
    private String mOutputVideoPath;


    long startPosition;
    long clipDur;


    OutputSurface outputSurface = null;
    InputSurface inputSurface = null;
    private int mFilterIndex;

    boolean isOpenBeauty;
    boolean videoFinish = false;
    boolean audioFinish = false;
    boolean released = false;
    long before;
    long after;
    Object lock = new Object();
    boolean muxStarted = false;
    OnVideoCutFinishListener listener;

    private Runnable videoCliper;
    private Runnable audioCliper;

    private boolean mVideoShake;
    private int mCurrentVideoShakeTimes = 0;
    private int mCurrentVideoShakeStep = 0;
    private long mVideoShakePointTimestamp;//鬼畜点的timestamp
    private double mCurrentShakeVideoTimestamp;//如果视频有鬼畜效果，需自己计算Timestamp供encoder使用


    private boolean mAudioShake;
    private int mCurrentAudioShakeTimes = 0;
    private int mCurrentAudioShakeStep = 0;
    private double mCurrentShakeAudioTimestamp;//如果视频有鬼畜效果，需自己计算Timestamp供encoder使用
    private int mAudioShakeStepLength;
    private long mAudioShakePointTimestamp;

    /**
     * @param outputKeyFrameInterval  关键帧间隔，单位：秒
     * @param outputVideoBitRate  视频比特率。
     *                      推荐 keyFrameInterval = 0 时   videoBitRate = 6_000_000
     *                          keyFrameInterval = 1 时   videoBitRate = 3_000_000
     */
    public VideoClipper(int outputKeyFrameInterval, int outputVideoBitRate) {
        try {
            videoDecoder = MediaCodec.createDecoderByType("video/avc");
            videoEncoder = MediaCodec.createEncoderByType("video/avc");
            audioDecoder = MediaCodec.createDecoderByType("audio/mp4a-latm");
            audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
        } catch (IOException e) {
            e.printStackTrace();
        }

        videoCliper = new VideoRunnable(outputKeyFrameInterval, outputVideoBitRate);
        audioCliper = new AudioRunnable();
    }

    /**
     * @param inputVideoPath
     * @param inputAudioPath
     */
    public void setInputVideoPath(@NonNull String inputVideoPath, @Nullable String inputAudioPath) {
        mInputVideoPath = inputVideoPath;
        mInputAudioPath = inputAudioPath;
        prepareExtractor();
    }


    private final String VIDEO_MIME_TYPE_PREFIX = "video/";
    private final String AUDIO_MIME_TYPE_PREFIX = "audio/";

    /** 视频 **/
    MediaExtractor videoExtractor;
    MediaCodec videoDecoder;
    MediaCodec videoEncoder;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotation;
    int videoFrameRate;
    private MediaFormat mVideoInputFormat;
    private long mVideoDurationTimestamp;
    private double mVideoFrameInterval;



    /** 音频 **/
    MediaExtractor audioExtractor;
    MediaCodec audioDecoder;
    MediaCodec audioEncoder;
    private MediaFormat mAudioInputFormat;
    private double mAudioSmapleInterval;


    /** muxer **/
    MediaMuxer mMediaMuxer;
    int muxVideoTrack = -1;
    int muxAudioTrack = -1;


    private void prepareExtractor() {
        videoExtractor = createExtractor(mInputVideoPath);
        int videoInputTrackIndex = getTrackIndex(videoExtractor, VIDEO_MIME_TYPE_PREFIX);
        videoExtractor.selectTrack(videoInputTrackIndex);
        mVideoInputFormat = videoExtractor.getTrackFormat(videoInputTrackIndex);
        mVideoWidth = mVideoInputFormat.getInteger(MediaFormat.KEY_WIDTH);
        mVideoHeight = mVideoInputFormat.getInteger(MediaFormat.KEY_HEIGHT);
        videoFrameRate = mVideoInputFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
        mVideoRotation = mVideoInputFormat.getInteger(MediaFormat.KEY_ROTATION);
        mVideoDurationTimestamp = mVideoInputFormat.getLong(MediaFormat.KEY_DURATION);

        mVideoFrameInterval = 1_000_000.0f / videoFrameRate;

        Log.e(TAG,"mVideoDurationTimestamp =" + mVideoDurationTimestamp +"  videoFrameRate = " + videoFrameRate + " mVideoFrameInterval = " + mVideoFrameInterval);

        if(mInputAudioPath == null){
            audioExtractor = createExtractor(mInputVideoPath);
        }else{
            audioExtractor = createExtractor(mInputAudioPath);
        }
        int audioInputTrackIndex = getTrackIndex(audioExtractor, AUDIO_MIME_TYPE_PREFIX);
        audioExtractor.selectTrack(audioInputTrackIndex);
        mAudioInputFormat = audioExtractor.getTrackFormat(audioInputTrackIndex);

        /**
         * 音频帧的播放时间=一个AAC帧对应的采样样本的个数/采样频率(单位为s)

         一帧 1024个 sample。采样率 Samplerate 44100Hz，每秒44100个sample, 所以根据公式 音频帧的播放时间=一个AAC帧对应的采样样本的个数/采样频率

         当前AAC一帧的播放时间是= 1024*1000/44100= 23.22ms(单位为ms)
         */
        mAudioSmapleInterval = 1024*1_000_000.0f / AUDIO_SAMPLE_RATE;

        mAudioShakeStepLength = (int) (mVideoFrameInterval * ShakeStack.VIDEO_SHAKE_STEP_LENGTH / mAudioSmapleInterval);
        audioExtractor.seekTo(ShakeStack.getInstance().getShakeTimestamp(), MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        mAudioShakePointTimestamp = audioExtractor.getSampleTime();
        audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

        Log.e(TAG,"mAudioShakeStepLength = " + mAudioShakeStepLength + "    mAudioShakePointTimestamp = " + mAudioShakePointTimestamp);
    }



    private MediaExtractor createExtractor(String dataSource) {
        MediaExtractor extractor = null;
        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(dataSource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractor;
    }


    private int getTrackIndex(MediaExtractor extractor, String mimeTypePrefix) {
        int trackCount = extractor.getTrackCount();
        for (int index = 0; index < trackCount; ++index) {
            if (getMimeType(extractor.getTrackFormat(index)).startsWith(mimeTypePrefix)) {
                return index;
            }
        }
        return -1;
    }

    private String getMimeType(MediaFormat format){
        return format.getString(MediaFormat.KEY_MIME);
    }

    public void setOutputVideoPath(String outputPath) {
        mOutputVideoPath = outputPath;
    }


    public void setOnVideoCutFinishListener(OnVideoCutFinishListener listener) {
        this.listener = listener;
    }
    /**
     * 设置滤镜
     * */
    public void setFilter(int filterIndex) {
        mFilterIndex = filterIndex;
    }


    /**
     * 处理整段视频

     * @throws IOException
     */
    public void clipVideo() throws IOException {
        clipVideo(0, mVideoDurationTimestamp);
    }


    /**
     * 裁剪视频
     *
     * @param startPosition 微秒级，截取开始位置
     * @param clipDur       微秒级，截取时长
     * @throws IOException
     */
    public void clipVideo(long startPosition, long clipDur) throws IOException {
        before = System.currentTimeMillis();
        this.startPosition = startPosition;
        this.clipDur = clipDur;

        mMediaMuxer = new MediaMuxer(mOutputVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        MediaThreadPool.executorService.execute(videoCliper);
        MediaThreadPool.executorService.execute(audioCliper);
    }


    private class VideoRunnable implements Runnable {

        private final int mOutputKeyFrameInterval;
        private final int mOutputVideoBitRate;

        public VideoRunnable(int outputKeyFrameInterval, int outputVideoBitRate){
            mOutputKeyFrameInterval = outputKeyFrameInterval;
            mOutputVideoBitRate = outputVideoBitRate;
        }

        @Override
        public void run() {
            long firstVideoTime = videoExtractor.getSampleTime();
            videoExtractor.seekTo(firstVideoTime + startPosition, SEEK_TO_PREVIOUS_SYNC);

            initVideoCodec(mOutputKeyFrameInterval, mOutputVideoBitRate);//暂时统一处理,为音频转换采样率做准备
            startVideoCodec(videoDecoder, videoEncoder, videoExtractor, inputSurface, outputSurface, firstVideoTime, startPosition, clipDur);

            videoFinish = true;
            release();
        }
    }


    private class AudioRunnable implements Runnable {
        @Override
        public void run() {
            initAudioCodec();
            startAudioCodec(audioDecoder, audioEncoder, audioExtractor, audioExtractor.getSampleTime(), startPosition, clipDur);
            audioFinish = true;
            release();
        }
    };


    private void initAudioCodec() {
        audioDecoder.configure(mAudioInputFormat, null, null, 0);
        audioDecoder.start();


        MediaFormat format = MediaFormat.createAudioFormat("audio/mp4a-latm", AUDIO_SAMPLE_RATE, /*channelCount*/AUDIO_CHANNEL_COUNT);//这里一定要注意声道的问题
        format.setInteger(MediaFormat.KEY_BIT_RATE, 128000);//比特率
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        audioEncoder.start();
    }

    /**
     *
     * @param decoder
     * @param encoder
     * @param extractor
     * @param firstSampleTime   第一个Sample的时间（PTS）
     * @param startPosition     截取开始位置
     * @param duration          截取时长
     */
    private void startAudioCodec(MediaCodec decoder, MediaCodec encoder, MediaExtractor extractor, long firstSampleTime, long startPosition, long duration) {
        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
        ByteBuffer[] decoderOutputBuffers = decoder.getOutputBuffers();
        ByteBuffer[] encoderInputBuffers = encoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
        MediaCodec.BufferInfo decoderBufferInfo = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo encoderBufferInfo = new MediaCodec.BufferInfo();
        boolean done = false;//用于判断整个编解码过程是否结束
        boolean inputDone = false;
        boolean decodeDone = false;
        int audioSampleCount = 0;
        extractor.seekTo(firstSampleTime + startPosition, SEEK_TO_PREVIOUS_SYNC);
        int decodeinput=0;
        int encodeinput=0;
        int encodeoutput=0;
        long lastEncodeOutputTimeStamp=-1;
        while (!done) {
            if (!inputDone) {
                int inputIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputIndex >= 0) {
                    long presentationTimeUS = extractor.getSampleTime();

                    ByteBuffer inputBuffer = decoderInputBuffers[inputIndex];
                    inputBuffer.clear();
                    int readSampleData = extractor.readSampleData(inputBuffer, 0);
                    long dur = extractor.getSampleTime() - firstSampleTime - startPosition;//当前截取的时长
                    if ((dur < duration) && readSampleData > 0) {
                        decoder.queueInputBuffer(inputIndex, 0, readSampleData, extractor.getSampleTime(), 0);
                        decodeinput++;
                        System.out.println("videoCliper audio decodeinput"+decodeinput+" dataSize"+readSampleData+" sampeTime"+extractor.getSampleTime());

                        if(presentationTimeUS == mAudioShakePointTimestamp && mCurrentAudioShakeTimes < ShakeStack.SHAKE_TIMES){
                            mAudioShake = true;
                        }

                        if(mAudioShake){
                            if(mCurrentAudioShakeStep < mAudioShakeStepLength){
                                ++mCurrentAudioShakeStep;
                                Log.i(TAG,"shake mCurrentAudioShakeStep = " + mCurrentAudioShakeStep);

                            }else{
                                extractor.seekTo(mAudioShakePointTimestamp, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

                                ++mCurrentAudioShakeTimes;
                                mCurrentAudioShakeStep = 0;

                                presentationTimeUS = mAudioShakePointTimestamp;
                                Log.e(TAG,"shake presentationTimeUS = " + presentationTimeUS + " mAudioShakePointTimestamp = " + mAudioShakePointTimestamp + "    newTimeStamp = " + videoExtractor.getSampleTime());

                                if(mCurrentAudioShakeTimes == ShakeStack.SHAKE_TIMES){
                                    mAudioShake = false;
                                    mCurrentAudioShakeTimes = 0;
                                    mCurrentAudioShakeStep = 0;
                                }
                            }
                        }

                        extractor.advance();
                    } else {
                        decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        System.out.println("videoCliper audio decodeInput end");
                        inputDone = true;
                    }
                }
            }
            if (!decodeDone) {
                int index = decoder.dequeueOutputBuffer(decoderBufferInfo, TIMEOUT_USEC);
                if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    decoderOutputBuffers = decoder.getOutputBuffers();
                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // expected before first buffer of data
                    MediaFormat newFormat = decoder.getOutputFormat();
                } else if (index < 0) {
                } else {
                    boolean canEncode = (decoderBufferInfo.size != 0 && decoderBufferInfo.presentationTimeUs - firstSampleTime > startPosition);
                    boolean endOfStream = (decoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                    if (canEncode&&!endOfStream) {
                        ByteBuffer decoderOutputBuffer = decoderOutputBuffers[index];

                        int encodeInputIndex = encoder.dequeueInputBuffer(TIMEOUT_USEC);

                        Log.e(TAG,"Cavalry encodeInputIndex = " + encodeInputIndex);

                        if(encodeInputIndex>=0){
                            ByteBuffer encoderInputBuffer = encoderInputBuffers[encodeInputIndex];
                            encoderInputBuffer.clear();
                            if (decoderBufferInfo.size < 4096) {//这里看起来应该是16位单声道转16位双声道
                                byte[] chunkPCM = new byte[decoderBufferInfo.size];
                                decoderOutputBuffer.get(chunkPCM);
                                decoderOutputBuffer.clear();
                                //说明是单声道的,需要转换一下
                                byte[] stereoBytes = new byte[decoderBufferInfo.size * 2];
                                for (int i = 0; i < decoderBufferInfo.size; i += 2) {
                                    stereoBytes[i * 2 + 0] = chunkPCM[i];
                                    stereoBytes[i * 2 + 1] = chunkPCM[i + 1];
                                    stereoBytes[i * 2 + 2] = chunkPCM[i];
                                    stereoBytes[i * 2 + 3] = chunkPCM[i + 1];
                                }
                                encoderInputBuffer.put(stereoBytes);
                                encoder.queueInputBuffer(encodeInputIndex, 0, stereoBytes.length, generateShakeAudioTimestamp(++audioSampleCount, decoderBufferInfo.presentationTimeUs), 0);
                                encodeinput++;
                                System.out.println("videoCliper audio encodeInput"+encodeinput+" dataSize"+decoderBufferInfo.size+" sampeTime"+decoderBufferInfo.presentationTimeUs);
                            }else{
                                encoderInputBuffer.put(decoderOutputBuffer);
                                encoder.queueInputBuffer(encodeInputIndex, decoderBufferInfo.offset, decoderBufferInfo.size, generateShakeAudioTimestamp(++audioSampleCount, decoderBufferInfo.presentationTimeUs), 0);
                                encodeinput++;
                                System.out.println("videoCliper audio encodeInput"+encodeinput+" dataSize"+decoderBufferInfo.size+" sampeTime"+decoderBufferInfo.presentationTimeUs);
                            }
                        }
                    }
                    if(endOfStream){
                        int encodeInputIndex = encoder.dequeueInputBuffer(TIMEOUT_USEC);
                        encoder.queueInputBuffer(encodeInputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        System.out.println("videoCliper audio encodeInput end");
                        decodeDone = true;
                    }
                    decoder.releaseOutputBuffer(index, false);
                }
            }
            boolean encoderOutputAvailable = true;
            while (encoderOutputAvailable) {
                int encoderStatus = encoder.dequeueOutputBuffer(encoderBufferInfo, TIMEOUT_USEC);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    encoderOutputAvailable = false;
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    encoderOutputBuffers = encoder.getOutputBuffers();
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = encoder.getOutputFormat();
                    startMux(newFormat, 1);
                } else if (encoderStatus < 0) {
                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    done = (encoderBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                    if (done) {
                        encoderOutputAvailable = false;
                    }
                    // Write the data to the output "file".
                    if (encoderBufferInfo.presentationTimeUs == 0 && !done) {
                        continue;
                    }
                    if (encoderBufferInfo.size != 0&&encoderBufferInfo.presentationTimeUs>0) {
                        /*encodedData.position(outputInfo.offset);
                        encodedData.limit(outputInfo.offset + outputInfo.size);*/
                        if(!muxStarted){
                            synchronized (lock){
                                if(!muxStarted){
                                    try {
                                        lock.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if(encoderBufferInfo.presentationTimeUs>lastEncodeOutputTimeStamp){//为了避免有问题的数据
                            encodeoutput++;
                            System.out.println("videoCliper audio encodeOutput"+encodeoutput+" dataSize"+encoderBufferInfo.size+" sampeTime"+encoderBufferInfo.presentationTimeUs);
                            mMediaMuxer.writeSampleData(muxAudioTrack, encodedData, encoderBufferInfo);
                            lastEncodeOutputTimeStamp=encoderBufferInfo.presentationTimeUs;
                        }
                    }

                    encoder.releaseOutputBuffer(encoderStatus, false);
                }
                if (encoderStatus != MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // Continue attempts to drain output.
                    continue;
                }
            }
        }
    }

    /**
     * 根据原音频的时间戳生成鬼畜音频的时间戳。如果音频没有鬼畜效果，则原样返回。
     * @param audioSampleCount
     * @param presentationTimeUs
     * @return
     */
    private long generateShakeAudioTimestamp(int audioSampleCount, long presentationTimeUs) {
        if(!ShakeStack.getInstance().hasShakeTimestamp()){
            return presentationTimeUs;
        }


        if(audioSampleCount <= 2){
            mCurrentShakeAudioTimestamp = presentationTimeUs;
        }else{
            mCurrentShakeAudioTimestamp += mAudioSmapleInterval;
        }

        if(presentationTimeUs == 4852970){
            Log.i(TAG,"Shake Audio audioSampleCount = "+ audioSampleCount +" presentationTimeUs = " + presentationTimeUs + " mCurrentShakeAudioTimestamp = " + mCurrentShakeAudioTimestamp);
        }else{
            Log.e(TAG,"Shake Audio audioSampleCount = "+ audioSampleCount +" presentationTimeUs = " + presentationTimeUs + " mCurrentShakeAudioTimestamp = " + mCurrentShakeAudioTimestamp);
        }


        return (long) mCurrentShakeAudioTimestamp;
    }

    private void initVideoCodec(int outputKeyFrameInterval, int outputVideoBitRate) {
        //不对视频进行压缩
        int encodeW = mVideoWidth;
        int encodeH = mVideoHeight;
        //设置视频的编码参数
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", encodeW, encodeH);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, outputVideoBitRate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, outputKeyFrameInterval);//每隔几秒一个关键帧
        videoEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        inputSurface = new InputSurface(videoEncoder.createInputSurface());
        inputSurface.makeCurrent();
        videoEncoder.start();

        outputSurface = new OutputSurface(mVideoWidth, mVideoHeight, mVideoRotation, this);

        outputSurface.setFilter(mFilterIndex);

        videoDecoder.configure(mVideoInputFormat, outputSurface.getSurface(), null, 0);
        videoDecoder.start();//解码器启动
    }

    /**
     * 将两个关键帧之间截取的部分重新编码
     *
     * @param decoder
     * @param encoder
     * @param extractor
     * @param inputSurface
     * @param outputSurface
     * @param firstSampleTime 视频第一帧的时间戳
     * @param startPosition   微秒级
     * @param duration        微秒级
     */
    private void startVideoCodec(MediaCodec decoder, MediaCodec encoder, MediaExtractor extractor, InputSurface inputSurface, OutputSurface outputSurface, long firstSampleTime, long startPosition, long duration) {
        if(HistoryConstans.mReverse){
            extractor.seekTo(mVideoDurationTimestamp, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }

        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo outputInfo = new MediaCodec.BufferInfo();
        boolean done = false;//用于判断整个编解码过程是否结束
        boolean inputDone = false;
        boolean decodeDone = false;
        int decodeFrameCount = 0;
        while (!done) {
            if (!inputDone) {
                int inputIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputIndex >= 0) {
                    long presentationTimeUS = extractor.getSampleTime();

                    ByteBuffer inputBuffer = decoderInputBuffers[inputIndex];
                    inputBuffer.clear();
                    int readSampleDataSize = extractor.readSampleData(inputBuffer, 0);
                    Log.e(TAG,"读取数据  9988   extractor.getSampleTime() = " + extractor.getSampleTime());

                    long dur = extractor.getSampleTime() - firstSampleTime - startPosition;//当前已经截取的视频长度
                    if (HistoryConstans.mReverse || (dur < duration) && readSampleDataSize > 0) {
                        decoder.queueInputBuffer(inputIndex, 0, readSampleDataSize, extractor.getSampleTime(), 0);

                        if(ShakeStack.getInstance().isShakeTimestamp(presentationTimeUS) && mCurrentVideoShakeTimes < ShakeStack.SHAKE_TIMES){
                            mVideoShakePointTimestamp = presentationTimeUS;
                            mVideoShake = true;
                        }

                        if(mVideoShake){
                            if(mCurrentVideoShakeStep < ShakeStack.VIDEO_SHAKE_STEP_LENGTH){
                                ++mCurrentVideoShakeStep;
                                Log.i(TAG,"shake mCurrentVideoShakeStep = " + mCurrentVideoShakeStep);

                            }else{
                                extractor.seekTo(mVideoShakePointTimestamp, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                                //生成视频的时候，音视频不同步，所以音频的SeekTo不再由视频控制
                                //audioExtractor.seekTo(mVideoShakePointTimestamp, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

                                ++mCurrentVideoShakeTimes;
                                mCurrentVideoShakeStep = 0;

                                presentationTimeUS = mVideoShakePointTimestamp;
                                Log.e(TAG,"shake presentationTimeUS = " + presentationTimeUS + " mVideoShakePointTimestamp = " + mVideoShakePointTimestamp + "    newTimeStamp = " + videoExtractor.getSampleTime());

                                if(mCurrentVideoShakeTimes == ShakeStack.SHAKE_TIMES){
                                    mVideoShake = false;
                                    mCurrentVideoShakeTimes = 0;
                                    mCurrentVideoShakeStep = 0;
                                }
                            }
                        }

                        if(HistoryConstans.mReverse){
                            extractor.seekTo((long) (presentationTimeUS - mVideoFrameInterval), MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                            Log.i(TAG," 反转 seekTo presentationTimeUS003 = " + presentationTimeUS);
                        }else{


                            extractor.advance();//转到下一帧
                            Log.i(TAG,"seekTo presentationTimeUS003 = " + presentationTimeUS);
                        }

                        if(HistoryConstans.mReverse){
                            if(presentationTimeUS == 0){
                                Log.e(TAG,"seekTo presentationTimeUS005 = " + presentationTimeUS);
                                inputIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                                decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                inputDone = true;
                            }
                        }
                    } else {
                        decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    }
                }
            }
            if (!decodeDone) {
                int index = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    //decoderOutputBuffers = decoder.getOutputBuffers();
                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // expected before first buffer of data
                    MediaFormat newFormat = decoder.getOutputFormat();
                } else if (index < 0) {
                } else {
                    boolean doRender = (info.size != 0 && info.presentationTimeUs - firstSampleTime > startPosition);
                    // #1.# releaseOutputBuffer之后，数据被发送到outputSurface
                    decoder.releaseOutputBuffer(index, doRender);
                    if (doRender) {
                        // This waits for the image and renders it after it arrives.
                        //#2.# drawImage, 将outputSurface上的数据二次绘制
                        outputSurface.awaitNewImage();
                        outputSurface.drawImage();
                        // Send it to the encoder.
                        Log.i(TAG,"解码数据 9988  presentationTimeUS003 info.presentationTimeUs = " + info.presentationTimeUs);
                        if(HistoryConstans.mReverse){//反转状态下
                            inputSurface.setPresentationTime((mVideoDurationTimestamp - info.presentationTimeUs) * 1000);
                        }else{//正常状态下
                            inputSurface.setPresentationTime(generateShakeVideoTimestamp_1000(++decodeFrameCount, info.presentationTimeUs));
                        }
                        //#3.# swapBuffers之后，outputSurface中的surface与inputSurface中的surface互换，数据被转移到encoder中
                        inputSurface.swapBuffers();
                    }
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        encoder.signalEndOfInputStream();
                        decodeDone = true;
                    }
                }
            }
            boolean encoderOutputAvailable = true;
            while (encoderOutputAvailable) {
                int encoderStatus = encoder.dequeueOutputBuffer(outputInfo, TIMEOUT_USEC);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    encoderOutputAvailable = false;
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    encoderOutputBuffers = encoder.getOutputBuffers();
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = encoder.getOutputFormat();
                    startMux(newFormat, 0);
                } else if (encoderStatus < 0) {
                } else {
                    ByteBuffer encodedBuffer = encoderOutputBuffers[encoderStatus];
                    done = (outputInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                    if (done) {
                        encoderOutputAvailable = false;
                    }
                    // Write the data to the output "file".
                    if (outputInfo.presentationTimeUs == 0 && !done) {
                        continue;
                    }
                    if (outputInfo.size != 0) {
                        encodedBuffer.position(outputInfo.offset);
                        encodedBuffer.limit(outputInfo.offset + outputInfo.size);
                        if(!muxStarted){
                            synchronized (lock){
                                if(!muxStarted){
                                    try {
                                        lock.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        mMediaMuxer.writeSampleData(muxVideoTrack, encodedBuffer, outputInfo);
                    }

                    encoder.releaseOutputBuffer(encoderStatus, false);
                }
                if (encoderStatus != MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // Continue attempts to drain output.
                    continue;
                }
            }
        }
    }

    /**
     *  根据原视频的时间戳，生成都鬼畜效果的视频的Timestamp，生产成的Timestamp已乘以1000。
     *  如果视频没有鬼畜效果，将原时间戳乘以1000返回
     * @param decodeFrameCount
     * @param presentationTimeUs
     * @return
     */
    private long generateShakeVideoTimestamp_1000(int decodeFrameCount, long presentationTimeUs) {
        if(!ShakeStack.getInstance().hasShakeTimestamp()){
            return presentationTimeUs * 1000;
        }

        if(decodeFrameCount <= 2){
            mCurrentShakeVideoTimestamp = presentationTimeUs;
        }else{
            mCurrentShakeVideoTimestamp += mVideoFrameInterval;
        }

        return (long) (mCurrentShakeVideoTimestamp * 1000);
    }

    /**
     * @param mediaFormat
     * @param flag        0 video,1 audio
     */
    private void startMux(MediaFormat mediaFormat, int flag) {
        if (flag == 0) {
            muxVideoTrack = mMediaMuxer.addTrack(mediaFormat);
        } else if (flag == 1) {
            muxAudioTrack = mMediaMuxer.addTrack(mediaFormat);
        }
        synchronized (lock) {
            if (muxAudioTrack != -1 && muxVideoTrack != -1 && !muxStarted) {
                mMediaMuxer.start();
                muxStarted = true;
                lock.notify();
            }
        }
    }

    private synchronized void release() {
        if (!videoFinish || !audioFinish || released) {
            return;
        }
        videoExtractor.release();
        audioExtractor.release();
        mMediaMuxer.stop();
        mMediaMuxer.release();
        if (outputSurface != null) {
            outputSurface.release();
        }
        if (inputSurface != null) {
            inputSurface.release();
        }
        videoDecoder.stop();
        videoDecoder.release();
        videoEncoder.stop();
        videoEncoder.release();
        audioDecoder.stop();
        audioDecoder.release();
        audioEncoder.stop();
        audioEncoder.release();
        released = true;
        after = System.currentTimeMillis();
        System.out.println("presentationTimeUS003  cutVideo count1=" + (after - before));
        if (listener != null) {
            listener.onFinish();
        }
    }

    public long getVideoTimestamp() {
        return videoExtractor.getSampleTime();
    }

    public interface OnVideoCutFinishListener {
        void onFinish();
    }
}
