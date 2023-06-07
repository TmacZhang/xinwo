package com.xjh.gestureheart.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.xinwo.xinview.ColorSeekBar;
import com.xinwo.xinview.history.EditStepDetail;
import com.xinwo.xinview.history.EditStepStack;
import com.xjh.gestureheart.mediacodec.CavalryMediaPlayer;
import com.xjh.gestureheart.mediacodec.VideoDrawer;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 25623 on 2018/3/15.
 */

public class VideoGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, View.OnTouchListener {
    private final String TAG = "VideoGLSurfaceView";

    private final Context context;
    private CavalryMediaPlayer mMediaPlayer;

    private boolean playerPrepared = false;

    private Surface mMediaSurface;
    private VideoDrawer mDrawer;
    private String mVideoOutputPath;
    private String mVideoInputPath;
    private ColorSeekBar mSeekBar;
    private Handler mMainHandler;
    private long mCurrentDrawingTimestamp;
    private boolean mIsSeekBarTouching;


    public VideoGLSurfaceView(Context context) {
        this(context, null);
    }

    public VideoGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        setOnTouchListener(this);

        mMediaPlayer = new CavalryMediaPlayer();
        mDrawer = new VideoDrawer(this);

        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void setVideoInputPath(String videoInputPath){
        File videoInputFile = new File(videoInputPath);
        if(!videoInputFile.exists()){
            Toast.makeText(getContext(),"视频文件不存在", Toast.LENGTH_LONG).show();
            return;
        }
        mVideoInputPath = videoInputPath;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mVideoInputPath);
        String rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        String widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        Log.e(TAG,"duration = " + duration + " MaxInteger = " + Integer.MAX_VALUE);    //这个duration与通过extractor获取的stamp/1_000_000f 不同

        int width = Integer.parseInt(widthStr);
        int height = Integer.parseInt(heightStr);
        int rotation = width > height ? 90 : 0;

        mDrawer.setVideoRotation(rotation);
    }

    public void setVideoOutputPath(String videoOutputPath){
        mVideoOutputPath = videoOutputPath;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.e(TAG,"onSurfaceCreated");
        mDrawer.onSurfaceCreated();
        SurfaceTexture surfaceTexture = mDrawer.getSurfaceTexture();
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Log.i(TAG," setOnFrameAvailableListener presentationTimeUS009 = " + mMediaPlayer.getVideoTimestampSequentNoMoveIndex());
                requestRender();
            }
        });
        mMediaSurface = new Surface(surfaceTexture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e(TAG,"onSurfaceChanged");

        mDrawer.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i(TAG," onDrawFrame presentationTimeUS009 = " + mMediaPlayer.getVideoTimestampSequentNoMoveIndex() +"   generateStampIndex = " + mMediaPlayer.generateStampIndex + "     consumeStampIndex = " + mMediaPlayer.consumeStampIndex);
        mDrawer.onDrawFrame();
    }


    private int mCurrentFilter = 0;
    private EditStepDetail mCurrentDetail = null;
    private boolean mAddNewFilter = false;

    /**
     * 播放视频
     * @param addNewFilter  true: 记录新的滤镜。  false: 不记录新的滤镜，使用旧滤镜
     */
    public void playVideo(boolean addNewFilter){
        if (!playerPrepared){
            mMediaPlayer.setDataSource(mVideoInputPath);
            mMediaPlayer.setSurface(mMediaSurface);
            mMediaPlayer.prepare();
            playerPrepared=true;
            if(mSeekBar!=null){
                mSeekBar.setMax((int) mMediaPlayer.getVideoDurationTimestamp());
            }
        }

        mAddNewFilter = addNewFilter;
        mDrawer.enableAddNewFilter(addNewFilter);

        Log.e(TAG,"playVideo    mAddNewFilter = " + mAddNewFilter);

        if(addNewFilter){
            //每次开始记录startTimestamp
            mCurrentDetail = new EditStepDetail();
            mCurrentDetail.startTimestamp = getVideoDecodedTimestamp();
            if(mCurrentDetail.startTimestamp == -1){
                mCurrentDetail.startTimestamp = 0;
            }
        }

        mMediaPlayer.start();
    }

    public void pauseVideo(){
        mMediaPlayer.pause();

        //每次暂停记录endTimestamp 和 filterIndex, 如果filterIndex为0，则不记录
        //记录完毕后将mCurrentFilter置为0
        if(mAddNewFilter){
            mCurrentDetail.endTimestamp = getVideoDecodedTimestamp();
            if(mCurrentDetail.endTimestamp == -1){//当视频播放到最后，timestamp会回到-1的位置
                mCurrentDetail.endTimestamp = getMaxVideoTimestamp();
            }
            mCurrentDetail.filterIndex = mCurrentFilter;
            EditStepStack.getInstance().addDetail(mCurrentDetail);
            mCurrentFilter = 0;
            mAddNewFilter = false;
            mDrawer.enableAddNewFilter(false);
        }

        //如果添加了粒子效果，每次暂停要删除program中的粒子数据
        mDrawer.clearParticles();
    }

    public void changeFilter(final int filterIndex){
        mCurrentFilter = filterIndex;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mDrawer.changeFilter(filterIndex);
            }
        });
    }

    /**
     * 获取当前解码的Timestamp(也即VideoDecodeRunnable中的刚执行videoExtractor.advance()之后获取的timestamp)
     * @return
     */
    public long getVideoDecodedTimestamp(){
        return mMediaPlayer.getVideoTimestamp();
    }

    public long getMaxVideoTimestamp(){
        return mMediaPlayer.getVideoDurationTimestamp();
    }

    /**
     * 获取正在VideoDrawer中处理的视频帧的timestamp
     * @return
     */
    public long getVideoTimestampSequent() {
        mCurrentDrawingTimestamp = mMediaPlayer.getVideoTimestampSequent();
        Log.e(TAG,"SeekBar mCurrentDrawingTimestamp = " + mCurrentDrawingTimestamp + "  generateStampIndex = " + mMediaPlayer.generateStampIndex + "     consumeStampIndex = " + mMediaPlayer.consumeStampIndex);
        if(mSeekBar!=null && !mIsSeekBarTouching){
            mSeekBar.setProgress((int) mCurrentDrawingTimestamp); // VideoDrawer中调用此方法获取进度
        }
        return mCurrentDrawingTimestamp;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDrawer.onTouch(event);
        return true;
    }

    public void setSeekBar(ColorSeekBar seekBar) {
        mSeekBar = seekBar;
        mSeekBar.setOnSeekBarChangeListener(new ColorSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(ColorSeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    Log.i(TAG,"VideoDecodeRunnable SeekBar fromUser = " + fromUser + "  progress = " + progress +"  isPlaying = " + isPlaying());
                }else{
                    Log.i(TAG,"VideoDecodeRunnable SeekBar fromUser = false false " + fromUser + "  progress = " + progress +"  isPlaying = " + isPlaying());
                }

                if(fromUser){
                    if(isPlaying()){
                        mMediaPlayer.seekTo(progress);
                    }else{
                        mMediaPlayer.seekTo2(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(ColorSeekBar seekBar) {
                mIsSeekBarTouching = true;
            }

            @Override
            public void onStopTrackingTouch(ColorSeekBar seekBar) {
                 mIsSeekBarTouching = false;
            }
        });
    }

    public void setProgress(int progress){
        mSeekBar.setProgress(progress);
    }

    public int getProgress(){
        return mSeekBar.getProgress();
    }

    public void reverse() {
        mMediaPlayer.reverse();
    }

    public void cancelReverse() {
        mMediaPlayer.cancelReverse();
    }


    public interface PlayStateChangeListener{
        void onPlayStart();
        void onPlayPause();
        void onPlayComplete();
    }

    private PlayStateChangeListener mPlayStateChangeListener;

    public void setOnPlayStateChangeListener(PlayStateChangeListener listener){
        mPlayStateChangeListener = listener;
        mMediaPlayer.setMediaCallback(new CavalryMediaPlayer.IMediaCallback() {
            @Override
            public void onDecodeStart() {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mPlayStateChangeListener != null){
                            mPlayStateChangeListener.onPlayStart();
                        }
                    }
                });
            }

            @Override
            public void onDecodePause() {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mPlayStateChangeListener != null){
                            mPlayStateChangeListener.onPlayPause();
                        }
                    }
                });
            }

            @Override
            public void onDecodeComplete() {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mPlayStateChangeListener != null){
                            mPlayStateChangeListener.onPlayComplete();
                            if(mSeekBar != null){
                                if(mMediaPlayer.isReverse()){
                                    mSeekBar.setProgress(0);
                                }else{
                                    mSeekBar.setProgress((int) mMediaPlayer.getVideoDurationTimestamp());
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    public void retrieveFilter(){
        EditStepStack.getInstance().retrieve();
        //TODO 重新定位视频流位置
        mMediaPlayer.seekTo2(EditStepStack.getInstance().getLastTimeStamp());
    }

    public void retrieveParticles(){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mDrawer.retrieveParticles();
            }
        });
    }

    public void enableNewFilter(boolean addNewFilter){
        mAddNewFilter = addNewFilter;
        if(mDrawer != null){
            mDrawer.enableAddNewFilter(mAddNewFilter);
        }
    }
}
