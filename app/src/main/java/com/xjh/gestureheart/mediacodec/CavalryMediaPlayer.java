package com.xjh.gestureheart.mediacodec;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by 25623 on 2018/2/1.
 */

public class CavalryMediaPlayer {
    private final String TAG = "CavalryMediaPlayer";

    private final String VIDEO_MIME_TYPE_PREFIX = "video/";
    private final String AUDIO_MIME_TYPE_PREFIX = "audio/";

    private static final String AUDIO_OUTPUT_MIME_TYPE = "audio/mp4a-latm"; // Advanced Audio Coding
    private static final int AUDIO_OUTPUT_CHANNEL_COUNT = 2; // Must match the input stream.
    private static final int AUDIO_OUTPUT_BIT_RATE = 128 * 1024;
    private static final int AUDIO_OUTPUT_AAC_PROFILE =
            MediaCodecInfo.CodecProfileLevel.AACObjectHE;
    private static final int AUDIO_OUTPUT_SAMPLE_RATE_HZ = 44100; // Must match the input stream.

    private String mDataSource;
    private Surface mVideoInputSurface;

    /** 视频 **/
    MediaExtractor videoExtractor;
    MediaCodec videoDecoder;
    private VideoDecodeRunnable videoDecodeRunnable;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotation;
    int videoFrameRate;
    long frameInterval;//每帧间隔时间
    private MediaFormat mVideoInputFormat;
    private long mVideoDurationTimestamp;



    /** 音频 **/
    MediaExtractor audioExtractor;
    MediaCodec audioDecoder;
    AudioTrack audioTrack;
    private AudioDecodeRunnable audioRunnable;
    private MediaFormat mAudioInputFormat;



    /** 视频和音频 **/
    static final int PLAY_STATE_UNSTARTED = 1;
    static final int PLAY_STATE_STARTED = 2;
    static final int PLAY_STATE_PAUSED = 3;
    static final int PLAY_STATE_COMPLETE = 4;
    static int mPlayState  = PLAY_STATE_UNSTARTED;

    private boolean mIsPlaying;

    private final int MAX_STAMPS_COUNT = 24;
    public long[] stamps = new long[MAX_STAMPS_COUNT];
    public int generateStampIndex = 0;
    public int consumeStampIndex = 0;
    private long mCurrentStamp = 0;
    private boolean mReverse;

    public CavalryMediaPlayer() {

    }

    public void setDataSource(String dataSource) {
        mDataSource = dataSource;
    }

    public void setSurface(Surface surface){
        mVideoInputSurface = surface;
    }


    public void prepare(){
        prepareExtractor();
        prepareVideoCodec();
        prepareAudioCodec();
        prepareAudioTrack();

        videoDecodeRunnable = new VideoDecodeRunnable(this);
        audioRunnable = new AudioDecodeRunnable(this);
        new Thread(videoDecodeRunnable).start();
        new Thread(audioRunnable).start();
    }

    public void start(){
        if(mPlayState == PLAY_STATE_STARTED)
            return;

        if(mPlayState == PLAY_STATE_UNSTARTED || mPlayState == PLAY_STATE_COMPLETE){
            generateStampIndex = 0;
            consumeStampIndex = 0;
        }

        if(mPlayState == PLAY_STATE_COMPLETE){
            videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }

        videoDecodeRunnable.startDecode();
        audioRunnable.startDecode();
    }

    public void pause(){
        if(mPlayState != PLAY_STATE_STARTED)
            return;

        videoDecodeRunnable.pauseDecode();
        Log.e(TAG,"pause666 videoExtractor.getSampleTime() = " + videoExtractor.getSampleTime());
        audioRunnable.pauseDecode();
    }

    public void reverse() {
        mReverse = true;
        videoDecodeRunnable.reverse();
        audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        audioRunnable.startDecode();
    }

    public void cancelReverse() {
        mReverse = false;
        videoDecodeRunnable.cancelReverse();
        audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
    }

    public boolean isReverse(){
        return mReverse;
    }

    public void stop(){

    }

    public void release(){

    }

    void setPlayState(int state){
        mPlayState = state;
    }

    public boolean isPlaying(){
        return mPlayState == PLAY_STATE_STARTED;
    }

    public long getVideoDurationTimestamp(){
        return mVideoDurationTimestamp;
    }

    public long getPositionTimestamp(){
        return videoExtractor.getSampleTime();
    }

    public int getVideoRotation(){
        return mVideoRotation;
    }

    /**
     * 获取当前videoExtractor的SampleTime
     * @return
     */
    public long getVideoTimestamp() {
        return videoExtractor == null ? 0 : videoExtractor.getSampleTime();
    }

    /**
     * 按顺序获取未使用的SampleTime
     * 每获取一次会将consumeStampIndex向下移动一个位置
     * @return
     */
    public long getVideoTimestampSequent(){
        mCurrentStamp = videoExtractor == null ? 0 : stamps[consumeStampIndex];
        consumeStampIndex = (++consumeStampIndex) % MAX_STAMPS_COUNT;
        return mCurrentStamp;
    }

    public long getVideoTimestampSequentNoMoveIndex(){
        mCurrentStamp = videoExtractor == null ? 0 : stamps[consumeStampIndex];
        return mCurrentStamp;
    }

    /**
     * 按顺序存放SampleTime
     * 每次会将generateStampIndex向后移动一个位置
     * @param timestamp
     */
    public void putVideoTimestampSequent(long timestamp){
        stamps[generateStampIndex] = timestamp;
        generateStampIndex = (++generateStampIndex) % MAX_STAMPS_COUNT;
    }

    public void seekTo(long time){
        videoExtractor.seekTo(time, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        audioExtractor.seekTo(time, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
    }

    public void seekTo2(long time){
        videoDecodeRunnable.seekTo(time);
        audioExtractor.seekTo(time, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
    }

    public int getVideoWidth(){
        return mVideoWidth;
    }

    public int getVideoHeight(){
        return mVideoHeight;
    }

    public int getVideoFrameRate(){
        return videoFrameRate;
    }


    private void prepareExtractor() {
        videoExtractor = createExtractor(mDataSource);
        int videoInputTrackIndex = getTrackIndex(videoExtractor, VIDEO_MIME_TYPE_PREFIX);
        videoExtractor.selectTrack(videoInputTrackIndex);
        mVideoInputFormat = videoExtractor.getTrackFormat(videoInputTrackIndex);

        Log.e(TAG,"mVideoInputFormat = " + mVideoInputFormat);

        mVideoWidth = mVideoInputFormat.getInteger(MediaFormat.KEY_WIDTH);

        Log.e(TAG,"mVideoInputFormat = " + mVideoInputFormat + "    mVideoWidth = " + mVideoWidth);


        mVideoHeight = mVideoInputFormat.getInteger(MediaFormat.KEY_HEIGHT);
        videoFrameRate = mVideoInputFormat.getInteger(MediaFormat.KEY_FRAME_RATE);

        mVideoRotation = 0;
//        mVideoRotation = mVideoInputFormat.getInteger(MediaFormat.KEY_ROTATION);
        mVideoDurationTimestamp = mVideoInputFormat.getLong(MediaFormat.KEY_DURATION);

        frameInterval = 1000 / videoFrameRate;

        Log.e(TAG,"mVideoDurationTimestamp =" + mVideoDurationTimestamp +"  videoFrameRate = " + videoFrameRate);

        audioExtractor = createExtractor(mDataSource);
        int audioInputTrackIndex = getTrackIndex(audioExtractor, AUDIO_MIME_TYPE_PREFIX);
        audioExtractor.selectTrack(audioInputTrackIndex);
        mAudioInputFormat = audioExtractor.getTrackFormat(audioInputTrackIndex);

      //  int audioFrameRate = mAudioInputFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
        int audioSampleRate = mAudioInputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        Log.i(TAG," audioSampleRate = " + audioSampleRate);
    }


    private void prepareVideoCodec(){
        try {
            videoDecoder = MediaCodec.createDecoderByType(getMimeType(mVideoInputFormat));
            videoDecoder.configure(mVideoInputFormat, mVideoInputSurface, null, 0);
            videoDecoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareAudioCodec(){
        try {
            audioDecoder = MediaCodec.createDecoderByType(getMimeType(mAudioInputFormat));
            audioDecoder.configure(mAudioInputFormat, null, null, 0);
            audioDecoder.start();

            MediaFormat audioOutputFormat =
                    MediaFormat.createAudioFormat(
                            AUDIO_OUTPUT_MIME_TYPE, AUDIO_OUTPUT_SAMPLE_RATE_HZ,
                            AUDIO_OUTPUT_CHANNEL_COUNT);
            audioOutputFormat.setInteger(MediaFormat.KEY_BIT_RATE, AUDIO_OUTPUT_BIT_RATE);
            audioOutputFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, AUDIO_OUTPUT_AAC_PROFILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void prepareAudioTrack(){
        //启动AudioTrack，这是个播放器，可以播放PCM格式的数据。如果有需要可以用到。不需要播放的直接删掉就可以了。
        int buffsize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        //创建AudioTrack对象
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                48000,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM);
        audioTrack.play();
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

    void onDecodeStart(){
        if(mMediaCallback != null){
            mMediaCallback.onDecodeStart();
        }
    }

    void onDecodePause(){
        if(mMediaCallback != null){
            mMediaCallback.onDecodePause();
            consumeStampIndex = 0;
            generateStampIndex = 0;
        }
    }

    void onDecodeComplete(){
        if(mMediaCallback != null){
            mMediaCallback.onDecodeComplete();
            consumeStampIndex = 0;
            generateStampIndex = 0;
        }
    }

    private IMediaCallback mMediaCallback;

    public void setMediaCallback(IMediaCallback mediaCallback){
        mMediaCallback = mediaCallback;
    }


    public interface IMediaCallback{
        void onDecodeStart();
        void onDecodePause();
        void onDecodeComplete();
    }
}
