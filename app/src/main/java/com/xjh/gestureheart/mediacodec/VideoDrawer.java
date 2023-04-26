package com.xjh.gestureheart.mediacodec;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.xjh.gestureheart.filter.BaseFilter;
import com.xjh.gestureheart.filter.CommonFilter;
import com.xjh.gestureheart.filter.ShowFilter;
import com.xjh.gestureheart.filter.particles.objects.ParticleShooter;
import com.xjh.gestureheart.filter.particles.objects.ParticleSystem;
import com.xjh.gestureheart.filter.particles.programs.ParticleShaderProgram;
import com.xjh.gestureheart.filter.particles.util.Geometry;
import com.xjh.gestureheart.filter.particles.util.TextureHelper;
import com.xjh.gestureheart.gl.VideoGLSurfaceView;
import com.xjh.gestureheart.history.EditStepStack;
import com.xjh.gestureheart.history.ParticlePoint;
import com.xjh.gestureheart.history.ParticleStack;
import com.xjh.xinwo.R;
import com.xjh.xinwo.base.BaseApplication;


/**
 * Created by 25623 on 2018/3/22.
 */

public class VideoDrawer{
    private final String TAG = "VideoDrawer";
    private VideoGLSurfaceView mVideoGLSurfaceView;
    private OutputSurface mOutputSurface;
    private Context context = BaseApplication.getInstance();

    private BaseFilter mCurFilter;
    private ShowFilter mShowFilter;
    private int mFilterIdx;
    private SurfaceTexture mSurfaceTexture;
    private int textureId;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotation;
    private boolean mAddNewFilter;
    private boolean mGenerateVideo;


    private ParticleShaderProgram particleProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private long globalStartTime;
    private int mHalfSurfaceWidth;
    private int mHalfSurfaceHeight;
    private int particlesTexture;
    private int mCurrentParticleStep;
    private boolean newParticleFirstPoint;

    public VideoDrawer(VideoGLSurfaceView videoGLSurfaceView){
        mVideoGLSurfaceView = videoGLSurfaceView;
        mGenerateVideo = false;
        mAddNewFilter = true;   //初次运行必然要在EditStepStack中添加新滤镜0
        init();
    }

    public VideoDrawer(OutputSurface outputSurface){
        mOutputSurface = outputSurface;
        mGenerateVideo = true;
        mAddNewFilter = false;
        init();

        Log.e(TAG,"889900 " + EditStepStack.getInstance().toString());
    }

    private void init(){
        mFilterIdx = 0;
        mCurFilter = getFilter(mFilterIdx);
        mShowFilter = new ShowFilter(context);
    }

    public void onSurfaceCreated() {
        GLES20.glClearColor(0, 0, 0, 0);

        mCurFilter.create();
        mShowFilter.onCreate();

        int[] textures = new int[1];
        GLES20.glGenTextures(1,textures, 0);
        if(textures[0] == 0){
            Log.e(TAG,"Can not generateEnable texture");
            return;
        }
        textureId = textures[0];
//        GLES20.glActiveTexture(textureId);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        /*
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES的用处？
        之前提到视频解码的输出格式是YUV的（YUV420p，应该是），那么这个扩展纹理的作用就是实现YUV格式到RGB的自动转化，
        我们就不需要再为此写YUV转RGB的代码了
        */
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        //GLES20.glBindTexture(textureId, 0); // 这里不需要解绑？


        mSurfaceTexture = new SurfaceTexture(textureId);

        particleProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(6000);
        globalStartTime = System.nanoTime();

        final Geometry.Vector particleDirection = new Geometry.Vector(1f, 1f, 1f);

        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 1f;

        redParticleShooter = new ParticleShooter(
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance);

        particlesTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    }

    public void onSurfaceChanged(int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mCurFilter.change(width, height);

        mHalfSurfaceWidth = width / 2;
        mHalfSurfaceHeight = height / 2;
    }

    private int mFrameCount = 0;

    public void onDrawFrame() {
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();


        //        float currentTime = (System.nanoTime() - globalStartTime) / 1_000_000_000f;
        long currentTimestamp = getCurrentTimestamp();
        float currentTime = currentTimestamp / 1_000_000f;

        Log.i(TAG,"presentationTimeUS008 onDrawFrame = getCurrentTimestamp() = " + currentTimestamp + "   currentTime = " + currentTime +"   frameCount = " + (++mFrameCount) );

        if(mAddNewFilter){
            mCurFilter.draw(textureId);
        }else{
            changeFilter(EditStepStack.getInstance().getFilter(currentTimestamp));
            mCurFilter.draw(textureId);
        }
        mShowFilter.onDraw(mCurFilter.getTextureId());




//        mCurFilter.draw(textureId);
//        mShowFilter.onDraw(mCurFilter.getTextureId());

        //新添加的粒子
        if(mIsTouching){
            //v_ElapsedTime = u_Time - a_ParticleStartTime; 这里的currentTime是粒子的开始时间

            ParticlePoint point = new ParticlePoint();

            point.particleFilterIndex = 0;
            point.step = mCurrentParticleStep;
            point.particlePointShowTimestamp = currentTimestamp;
            point.x = mPointX;
            point.y = mPointY;
            point.z = 0f;

            ParticleStack.getInstance().addPoint(point);
            if(newParticleFirstPoint){
                ParticleStack.getInstance().putParticleStartTimestamp(mCurrentParticleStep, currentTimestamp);
            }
        }



        SparseArray<ParticlePoint> points = ParticleStack.getInstance().getPoints(currentTimestamp);

        if(points != null){
            for(int i = 0; i< points.size(); i++){
                Log.e(TAG,"presentationTimeUS008 = "+ currentTimestamp +" point = " + points.valueAt(i).toString());
                redParticleShooter.addParticles(particleSystem, points.valueAt(i), 6);
            }
        }
        //播放旧的粒子
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);

        particleProgram.useProgram();
        //v_ElapsedTime = u_Time - a_ParticleStartTime; 这里的currentTime是uTime
        particleProgram.setUniforms(currentTime, particlesTexture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public SurfaceTexture getSurfaceTexture(){
        return mSurfaceTexture;
    }

    public void changeFilter(int filterIndex) {
        if(mFilterIdx == filterIndex)
            return;

        mFilterIdx = filterIndex;

        Log.e(TAG,"changeFilter  mFilterIdx = " + mFilterIdx);

        mCurFilter = getFilter(mFilterIdx);
        mCurFilter.create();
        mCurFilter.change(mSurfaceWidth, mSurfaceHeight);
        mCurFilter.setRotation(mVideoRotation);
    }

    public void setVideoRotation(int videoRotation) {
        this.mVideoRotation = videoRotation;
        mCurFilter.setRotation(videoRotation);
    }

    private int[] mFilterFragSourceID = new int[]{
            R.raw.no_filter_fragment,   //0
            R.raw.endless_01,
            R.raw.water_ripple,
            R.raw.drunk,
            R.raw.edge_glow,
            R.raw.mirror,
            R.raw.seperate_rgb,
            R.raw.basic_deform,
            R.raw.blue_orange,
            R.raw.change,
            R.raw.cracked,
            R.raw.edge_detection,//10
            R.raw.new_edge_dection,
            R.raw.rainbow,
            R.raw.simulation,
            R.raw.tv,
            R.raw.diffusion,//15
    };

    private BaseFilter getFilter(int index){
        return new CommonFilter(context, mFilterFragSourceID[index]);
    }

    public void release() {
        if(mSurfaceTexture != null){
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if(mCurFilter != null){
            mCurFilter.destory();
            mCurFilter = null;
        }

        if(mShowFilter != null){
            mShowFilter.destory();
            mShowFilter = null;
        }
    }

    /**
     * @param addNewFilter  true:正在增加新滤镜    false: 没有正在增加新滤镜，使用存储的EditStepStack中的滤镜
     */
    public void enableAddNewFilter(boolean addNewFilter) {
        mAddNewFilter = addNewFilter;
    }

    public long getCurrentTimestamp(){
        if(mGenerateVideo){
            return mOutputSurface.getVideoTimeStamp();
        }else {
            return mVideoGLSurfaceView.getVideoTimestampSequent();
        }
    }

    private boolean mIsTouching = false;
    private float mPointX;
    private float mPointY;

    public void onTouch(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mCurrentParticleStep = ParticleStack.getInstance().getCurrentStep() + 1;
                newParticleFirstPoint = true;
            case MotionEvent.ACTION_MOVE:
                mPointX = event.getX()/ mHalfSurfaceWidth - 1;
                mPointY = -(event.getY() / mHalfSurfaceHeight -1);
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_UP:
                mIsTouching = false;
                break;
        }

    }

    public void clearParticles() {
        particleSystem.removeParticles();
    }

    public void retrieveParticles(){
        ParticleStack.getInstance().retrieve(); //回删数据
        mVideoGLSurfaceView.setProgress((int) ParticleStack.getInstance().getRetrievedParticleStartTimestamp());//返回到上一次的进度
        particleSystem.updateParticles(ParticleStack.getInstance().getRetrievedParticleStartTimestamp());
    }
}
