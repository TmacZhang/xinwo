package com.xinwo.produce.record;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.faceunity.wrapper.faceunity;
import com.xinwo.application.XinApplicationUtil;
import com.xinwo.produce.R;
import com.xinwo.produce.gestureheart.activity.PreviewActivity;
import com.xinwo.produce.music.ui.MusicActivity;
import com.xinwo.produce.record.encoder.TextureMovieEncoder;
import com.xinwo.produce.record.gles.FullFrameRect;
import com.xinwo.produce.record.gles.GlUtil;
import com.xinwo.produce.record.gles.LandmarksPoints;
import com.xinwo.produce.record.gles.Texture2dProgram;
import com.xinwo.produce.record.utils.CameraUtils;
import com.xinwo.produce.record.utils.Constants;
import com.xinwo.produce.record.utils.FPSUtil;
import com.xinwo.produce.record.utils.MiscUtil;
import com.xinwo.produce.record.utils.SectionRecordTool;
import com.xinwo.produce.record.utils.VideoUtils;
import com.xinwo.produce.record.view.AspectFrameLayout;
import com.xinwo.produce.record.view.EffectAndFilterSelectAdapter;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 这个Activity演示了faceunity的使用
 * 本demo中draw方法要求在子类集成实现绘制方法（便于不同绘制方法的展示），实际使用中draw无需在子类实现。
 * <p>
 * Created by lirui on 2016/12/13.
 */

@SuppressWarnings("deprecation")
public abstract class FUExampleFragment extends FUBaseUIFragment
        implements Camera.PreviewCallback {


    private boolean mBeauty = true;
    private int mSpeed = TextureMovieEncoder.SPEED_NORMAL;
    private boolean mIsRecording;
    List<String> tmpVideoPathList = new ArrayList<>();
    private String merge_path;
    public static final String MERGE_PATH = "mergePath";


    protected abstract int draw(byte[] cameraNV21Byte, byte[] fuImgNV21Bytes, int cameraTextureId, int cameraWidth,
                                int cameraHeight, int frameId, int[] ints, int currentCameraType);

    public final static String TAG = FUExampleFragment.class.getSimpleName();

    private FUExampleFragment mContext;

    private GLSurfaceView mGLSurfaceView;
    private GLRenderer mGLRenderer;
    private int mViewWidth;
    private int mViewHeight;

    private Camera mCamera;
    private int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mCameraOrientation;
    private int mCameraWidth = 1280;
    private int mCameraHeight = 720;

    private static final int PREVIEW_BUFFER_COUNT = 3;
    private byte[][] previewCallbackBuffer;

    private byte[] mCameraNV21Byte;
    private byte[] mFuImgNV21Bytes;

    private int mFrameId = 0;

    private int mFaceBeautyItem = 0; //美颜道具
    private int mEffectItem = 0; //贴纸道具

    private float mFilterLevel = 1.0f;
    private float mFaceBeautyColorLevel = 0.2f;
    private float mFaceBeautyBlurLevel = 6.0f;
    private float mFaceBeautyALLBlurLevel = 0.0f;
    private float mFaceBeautyCheekThin = 1.0f;
    private float mFaceBeautyEnlargeEye = 0.5f;
    private float mFaceBeautyRedLevel = 0.5f;
    private int mFaceShape = 3;
    private float mFaceShapeLevel = 0.5f;

    private String mFilterName = EffectAndFilterSelectAdapter.FILTERS_NAME[0];

    private boolean isNeedEffectItem = true;
    private String mEffectFileName = EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[1];

    private TextureMovieEncoder mTextureMovieEncoder;
    private String mVideoFileName;

    private HandlerThread mCreateItemThread;
    private Handler mCreateItemHandler;

    private boolean isInPause = true;

    private boolean isInAvatarMode = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        mContext = this;
        super.onCreate(savedInstanceState);

        mGLSurfaceView = (GLSurfaceView) getView().findViewById(R.id.glsv);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLRenderer = new GLRenderer();
        mGLSurfaceView.setRenderer(mGLRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mCreateItemThread = new HandlerThread("CreateItemThread");
        mCreateItemThread.start();
        mCreateItemHandler = new CreateItemHandler(mCreateItemThread.getLooper());
    }


    @Override
    public void onResume() {
        Log.e(TAG, "onResume");

        super.onResume();

        openCamera(mCurrentCameraType, mCameraWidth, mCameraHeight);

        mGLSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");

        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);

        releaseCamera();

        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mGLRenderer.notifyPause();
                mGLRenderer.destroySurfaceTexture();

                mEffectItem = 0;
                mFaceBeautyItem = 0;
                //Note: 切忌使用一个已经destroy的item
                faceunity.fuDestroyAllItems();
                isNeedEffectItem = true;
                faceunity.fuOnDeviceLost();
                mFrameId = 0;
            }
        });

        mGLSurfaceView.onPause();

        FPSUtil.reset();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        mEffectFileName = EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[1];

        mCreateItemThread.quitSafely();
        mCreateItemThread = null;
        mCreateItemHandler = null;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCameraNV21Byte = data;
        mCamera.addCallbackBuffer(data);
        isInPause = false;
    }

    class GLRenderer implements GLSurfaceView.Renderer {

        FullFrameRect mFullScreenFUDisplay;
        FullFrameRect mCameraDisplay;

        int mCameraTextureId;
        SurfaceTexture mCameraSurfaceTexture;

        int faceTrackingStatus = 0;
        int systemErrorStatus = 0;//success number
        float[] isCalibrating = new float[1];

        LandmarksPoints landmarksPoints;
        float[] landmarksData = new float[150];
        float[] expressionData = new float[46];
        float[] rotationData = new float[4];
        float[] pupilPosData = new float[2];
        float[] rotationModeData = new float[1];

        int fuTex;
        final float[] mtx = new float[16];
        float[] projectionMatrix = GlUtil.IDENTITY_MATRIX;
        private int frameAvailableCount;
        private FloatBuffer vertexArray;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.e(TAG, "onSurfaceCreated fu version " + faceunity.fuGetVersion());

            mFullScreenFUDisplay = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_2D));
            mCameraDisplay = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_EXT));
            mCameraTextureId = mCameraDisplay.createTextureObject();

            landmarksPoints = new LandmarksPoints();//如果有证书权限可以获取到的话，绘制人脸特征点

            switchCameraSurfaceTexture();

            try {
                InputStream is = getContext().getAssets().open("v3.bundle");
                byte[] v3data = new byte[is.available()];
                int len = is.read(v3data);
                is.close();
                faceunity.fuSetup(v3data, null, authpack.A());
                //faceunity.fuSetMaxFaces(1);//设置最大识别人脸数目
                Log.e(TAG, "fuSetup v3 len " + len);

                is = getContext().getAssets().open("anim_model.bundle");
                byte[] animModelData = new byte[is.available()];
                is.read(animModelData);
                is.close();
                faceunity.fuLoadAnimModel(animModelData);
                faceunity.fuSetExpressionCalibration(1);

                is = getContext().getAssets().open("face_beautification.bundle");
                byte[] itemData = new byte[is.available()];
                len = is.read(itemData);
                Log.e(TAG, "beautification len " + len);
                is.close();
                mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.e(TAG, "onSurfaceChanged " + width + " " + height);
            GLES20.glViewport(0, 0, width, height);
            mViewWidth = width;
            mViewHeight = height;

            vertexArray = generateVertexArray(width, height, mCameraHeight, mCameraWidth);
//            if(width > height){
//                float aspectRatio = 1.0f * width / height;
//                Matrix.orthoM(projectionMatrix,  0, -aspectRatio, aspectRatio, -1.0f, 1.0f, -1.0f, 1.0f);
//            }else{
//                float aspectRatio = 1.0f * height / width;
//                Matrix.orthoM(projectionMatrix,  0, -1.0f, 1.0f, -aspectRatio, aspectRatio, -1.0f, 1.0f);
//            }
        }

        //2019-12-08 ： 适配相机预览被拉伸或压缩的情况
        private FloatBuffer generateVertexArray(int viewWidth, int viewHeight, int picWidth, int picHeight) {
            float widthRatio = 1.0f * viewWidth / picWidth;
            float heightRatio = 1.0f * viewHeight / picHeight;


            float[] coordinates;

            if (widthRatio > heightRatio) {
                float yExtra = ((widthRatio * picHeight - viewHeight) / viewHeight) / 2;
                coordinates = new float[]{
                        -1.0f, -1.0f - yExtra,   // 0 bottom left
                        1.0f, -1.0f - yExtra,   // 1 bottom right
                        -1.0f, 1.0f + yExtra,   // 2 top left
                        1.0f, 1.0f + yExtra,   // 3 top right
                };
            } else if (heightRatio > widthRatio) {
                float xExtra = ((heightRatio * picWidth - viewWidth) / viewWidth) / 2;
                coordinates = new float[]{
                        -1.0f - xExtra, -1.0f,   // 0 bottom left
                        1.0f + xExtra, -1.0f,   // 1 bottom right
                        -1.0f - xExtra, 1.0f,   // 2 top left
                        1.0f + xExtra, 1.0f,   // 3 top right
                };
            } else {
                coordinates = new float[]{
                        -1.0f, -1.0f,   // 0 bottom left
                        1.0f, -1.0f,   // 1 bottom right
                        -1.0f, 1.0f,   // 2 top left
                        1.0f, 1.0f,   // 3 top right
                };
            }

            for (int i = 0; i < 4; ++i) {
                Log.e(TAG, "COORDINATES -->  " + coordinates[2 * i] + ", " + coordinates[2 * i + 1]);
            }

            final int BYTES_PER_FLOAT = 4;

            ByteBuffer bb = ByteBuffer.allocateDirect(coordinates.length * BYTES_PER_FLOAT);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer fb = bb.asFloatBuffer();
            fb.put(coordinates);
            fb.position(0);

            return fb;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            FPSUtil.fps(TAG);

            if (isInPause) {
                mFullScreenFUDisplay.drawFrame(fuTex, projectionMatrix, mtx, vertexArray);
                mGLSurfaceView.requestRender();
                return;
            }

            /**
             * 获取camera数据, 更新到texture
             */
            try {
                mCameraSurfaceTexture.updateTexImage();
                mCameraSurfaceTexture.getTransformMatrix(mtx);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final int isTracking = faceunity.fuIsTracking();
            if (isTracking != faceTrackingStatus) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isTracking == 0) {
                            Log.e(TAG, "未检测到人脸");
                            Arrays.fill(landmarksData, 0);
                        } else {
                            Log.i(TAG, "检测到人脸");
                        }
                    }
                });
                faceTrackingStatus = isTracking;
            }

            final int systemError = faceunity.fuGetSystemError();
            if (systemError != systemErrorStatus) {
                systemErrorStatus = systemError;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "system error " + systemError + " " + faceunity.fuGetSystemErrorString(systemError));
                    }
                });
            }

            if (isNeedEffectItem) {
                isNeedEffectItem = false;
                mCreateItemHandler.sendMessage(Message.obtain(mCreateItemHandler, CreateItemHandler.HANDLE_CREATE_ITEM, mEffectFileName));
            }

            if (mBeauty) {
                faceunity.fuItemSetParam(mFaceBeautyItem, "filter_level", mFilterLevel);
                faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", mFaceBeautyColorLevel);
                faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", mFaceBeautyBlurLevel);
                faceunity.fuItemSetParam(mFaceBeautyItem, "skin_detect", mFaceBeautyALLBlurLevel);
                faceunity.fuItemSetParam(mFaceBeautyItem, "filter_name", mFilterName);
                faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", mFaceBeautyCheekThin);
                faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", mFaceBeautyEnlargeEye);
                faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", mFaceShape);
                faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", mFaceShapeLevel);
                faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", mFaceBeautyRedLevel);
            } else {
                faceunity.fuItemSetParam(mFaceBeautyItem, "filter_level", 0);
                faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", 0);
                faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", 0);
                faceunity.fuItemSetParam(mFaceBeautyItem, "skin_detect", 0);
                faceunity.fuItemSetParam(mFaceBeautyItem, "filter_name", mFilterName);
                faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", 0);
                faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", 0);
                faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", 0);
                faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", 0);
                faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", 0);
            }


            if (mCameraNV21Byte == null || mCameraNV21Byte.length == 0) {
                Log.e(TAG, "camera nv21 bytes null");
                mGLSurfaceView.requestRender();
                return;
            }

            if (isInAvatarMode) {
                /**
                 * Avatar道具推荐使用 fuTrackFace 与 fuAvatarToTexture 的api组合
                 */
                fuTex = drawAvatar();
            } else {
                fuTex = draw(mCameraNV21Byte, mFuImgNV21Bytes, mCameraTextureId, mCameraWidth, mCameraHeight, mFrameId++, new int[]{mFaceBeautyItem, mEffectItem}, mCurrentCameraType);
            }

            mFullScreenFUDisplay.drawFrame(fuTex, projectionMatrix, mtx, vertexArray);

            /**
             * 绘制Avatar模式下的镜头内容以及landmarks
             **/
            if (isInAvatarMode) {
                int[] originalViewport = new int[4];
                GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, originalViewport, 0);
                GLES20.glViewport(0, mViewHeight * 2 / 3, mViewWidth / 3, mViewHeight / 3);
                mCameraDisplay.drawFrame(mCameraTextureId, projectionMatrix, mtx, vertexArray);
                landmarksPoints.draw();
                GLES20.glViewport(originalViewport[0], originalViewport[1], originalViewport[2], originalViewport[3]);
            }

            final float[] isCalibratingTmp = new float[1];
            faceunity.fuGetFaceInfo(0, "is_calibrating", isCalibratingTmp);
            if (isCalibrating[0] != isCalibratingTmp[0]) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ((isCalibrating[0] = isCalibratingTmp[0]) > 0 && EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[6].equals(mEffectFileName)) {
                            isCalibratingText.setVisibility(View.VISIBLE);
                            isCalibratingText.setText(strCalibrating);
                            showNum = 0;
                            isCalibratingText.postDelayed(mCalibratingRunnable, 500);
                        } else {
                            isCalibratingText.removeCallbacks(mCalibratingRunnable);
                            isCalibratingText.setVisibility(View.GONE);
                        }
                    }
                });
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(TextureMovieEncoder.START_RECORDING)) {
                mVideoFileName = VideoUtils.createTempOutputFile4Video(Constants.getVideoRecordTmpPath(XinApplicationUtil.Companion.getInstance().getMApplication()),
                        tmpVideoPathList.size());
                tmpVideoPathList.add(mVideoFileName);
                File outFile = new File(mVideoFileName);
                if (outFile.exists()) {
                    outFile.delete();
                }

                mTextureMovieEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        outFile, mCameraHeight, mCameraWidth,
                        3000000, EGL14.eglGetCurrentContext(), mCameraSurfaceTexture.getTimestamp()
                ));
                mTextureMovieEncoder.setTextureId(mFullScreenFUDisplay, fuTex, mtx);
                mTextureMovieEncoder.setSpeed(TextureMovieEncoder.SPEED_NORMAL);
                //forbid click until start or stop success
                mTextureMovieEncoder.setOnEncoderStatusUpdateListener(new TextureMovieEncoder.OnEncoderStatusUpdateListener() {
                    @Override
                    public void onStartSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "start encoder success");
                            }
                        });
                    }

                    @Override
                    public void onStopSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "stop encoder success");
                            }
                        });
                    }
                });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "video file saved to "
                                + mVideoFileName, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(TextureMovieEncoder.IN_RECORDING)) {
                ++frameAvailableCount;
                Log.e(TAG, "PRPR --> frameAvailable  = " + frameAvailableCount);
                mTextureMovieEncoder.setTextureId(mFullScreenFUDisplay, fuTex, mtx);
                mTextureMovieEncoder.frameAvailable(mCameraSurfaceTexture);
            }

            mGLSurfaceView.requestRender();

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.isRecording() && mIsRecording) {

                long startTimeMillis = SectionRecordTool.getInstance().getCurrentStartRecrodTimeMillis();
                long currentTimeMillis = System.currentTimeMillis();
                SectionRecordTool.getInstance().setCurrentTotalRecordTimeMillis(currentTimeMillis - startTimeMillis + SectionRecordTool.getInstance().getLastSectionIndex());

                Log.e(TAG, "startTimeMillis = " + startTimeMillis
                        + "      currentTimeMillis = " + currentTimeMillis
                        + "     差值 = " + (currentTimeMillis - startTimeMillis)
                        + "     TotalRecordTimeMillis = " + SectionRecordTool.getInstance().getCurrentTotalRecordTimeMillis());


                if (SectionRecordTool.getInstance().getCurrentTotalRecordTimeMillis() >= MAX_RECORD_DURATION_MILLIS) {
                    SectionRecordTool.getInstance().setCurrentTotalRecordTimeMillis(MAX_RECORD_DURATION_MILLIS);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sectionProgressBar.setCurrentProgress(MAX_RECORD_DURATION_MILLIS);
                            mRecordingBtn.performClick();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sectionProgressBar.setCurrentProgress(SectionRecordTool.getInstance().getCurrentTotalRecordTimeMillis());
                        }
                    });
                }
            }
        }

        int drawAvatar() {
            faceunity.fuTrackFace(mCameraNV21Byte, 0, mCameraWidth, mCameraHeight);

            /**
             * landmarks
             */
            Arrays.fill(landmarksData, 0.0f);
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
            if (landmarksPoints != null) {
                landmarksPoints.refresh(landmarksData, mCameraWidth, mCameraHeight, mCameraOrientation, mCurrentCameraType);
            }

            /**
             *rotation
             */
            Arrays.fill(rotationData, 0.0f);
            faceunity.fuGetFaceInfo(0, "rotation", rotationData);
            /**
             * expression
             */
            Arrays.fill(expressionData, 0.0f);
            faceunity.fuGetFaceInfo(0, "expression", expressionData);

            /**
             * pupil pos
             */
            Arrays.fill(pupilPosData, 0.0f);
            faceunity.fuGetFaceInfo(0, "pupil_pos", pupilPosData);

            /**
             * rotation mode
             */
            Arrays.fill(rotationModeData, 0.0f);
            faceunity.fuGetFaceInfo(0, "rotation_mode", rotationModeData);

            int isTracking = faceunity.fuIsTracking();

            //rotation 是一个4元数，如果还没获取到，就使用1,0,0,0
            if (isTracking <= 0) {
                rotationData[3] = 1.0f;
            }

            /**
             * adjust rotation mode
             */
            if (isTracking <= 0) {
                rotationModeData[0] = (360 - mCameraOrientation) / 90;
            }

            return faceunity.fuAvatarToTexture(pupilPosData,
                    expressionData,
                    rotationData,
                    rotationModeData,
                    /*flags*/0,
                    mCameraWidth,
                    mCameraHeight,
                    mFrameId++,
                    new int[]{mEffectItem},
                    isTracking);
        }

        public void switchCameraSurfaceTexture() {
            Log.e(TAG, "switchCameraSurfaceTexture");
            if (mCameraSurfaceTexture != null) {
                faceunity.fuOnCameraChange();
                destroySurfaceTexture();
            }
            mCameraSurfaceTexture = new SurfaceTexture(mCameraTextureId);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handleCameraStartPreview(mCameraSurfaceTexture);
                }
            });
        }

        public void notifyPause() {
            faceTrackingStatus = 0;

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(TextureMovieEncoder.IN_RECORDING)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecordingBtn.performClick();
                    }
                });
            }

            if (mFullScreenFUDisplay != null) {
                mFullScreenFUDisplay.release(false);
                mFullScreenFUDisplay = null;
            }

            if (mCameraDisplay != null) {
                mCameraDisplay.release(false);
                mCameraDisplay = null;
            }
        }

        public void destroySurfaceTexture() {
            if (mCameraSurfaceTexture != null) {
                mCameraSurfaceTexture.release();
                mCameraSurfaceTexture = null;
            }
        }
    }

    class CreateItemHandler extends Handler {

        static final int HANDLE_CREATE_ITEM = 1;

        CreateItemHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_CREATE_ITEM:
                    try {
                        final String effectFileName = (String) msg.obj;
                        final int newEffectItem;
                        if (effectFileName.equals("none")) {
                            newEffectItem = 0;
                        } else {
                            InputStream is = getContext().getAssets().open(effectFileName);
                            byte[] itemData = new byte[is.available()];
                            int len = is.read(itemData);
                            Log.e(TAG, "effect len " + len);
                            is.close();
                            newEffectItem = faceunity.fuCreateItemFromPackage(itemData);
                            mGLSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    faceunity.fuItemSetParam(newEffectItem, "isAndroid", 1.0);
                                    faceunity.fuItemSetParam(newEffectItem, "rotationAngle", 360 - mCameraOrientation);
                                }
                            });
                        }
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (mEffectItem != 0 && mEffectItem != newEffectItem) {
                                    faceunity.fuDestroyItem(mEffectItem);
                                }
                                isInAvatarMode = Arrays.asList(EffectAndFilterSelectAdapter.AVATAR_EFFECT).contains(effectFileName);
                                mEffectItem = newEffectItem;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void openCamera(int cameraType, int desiredWidth, int desiredHeight) {
        Log.e(TAG, "openCamera");

        if (mCamera != null) {
            throw new RuntimeException("camera already initialized");
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = 0;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraType) {
                cameraId = i;
                mCamera = Camera.open(i);
                mCurrentCameraType = cameraType;
                break;
            }
        }
        if (mCamera == null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),
                                    "Open Camera Failed! Make sure it is not locked!", Toast.LENGTH_SHORT)
                            .show();
                }
            });
            throw new RuntimeException("unable to open camera");
        }

        mCameraOrientation = CameraUtils.getCameraOrientation(cameraId);
        CameraUtils.setCameraDisplayOrientation(getActivity(), cameraId, mCamera);

        Camera.Parameters parameters = mCamera.getParameters();

        CameraUtils.setFocusModes(parameters);
        CameraUtils.chooseFramerate(parameters, 15);
        int[] size = CameraUtils.choosePreviewSize(parameters, desiredWidth, desiredHeight);
        mCameraWidth = size[0];
        mCameraHeight = size[1];

        int fps[] = new int[2];
        parameters.getPreviewFpsRange(fps);
        parameters.setRecordingHint(true);
        Log.e(TAG, "CameraInfo desiredWidth = " + desiredWidth + "   desireHeight = " + desiredHeight + " mCameraWidth = " + mCameraWidth + "   mCameraHeight = " + mCameraHeight
                + "    minFPS = " + fps[0] + "   maxFPS = " + fps[1]);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AspectFrameLayout aspectFrameLayout = (AspectFrameLayout) getView().findViewById(R.id.afl);
                aspectFrameLayout.setAspectRatio(1.0f * mCameraHeight / mCameraWidth);
            }
        });

        mCamera.setParameters(parameters);
    }

    /**
     * set preview and start preview after the surface created
     */
    private void handleCameraStartPreview(SurfaceTexture surfaceTexture) {
        Log.e(TAG, "handleCameraStartPreview");

        if (previewCallbackBuffer == null) {
            Log.e(TAG, "allocate preview callback buffer");
            previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight * 3 / 2];
        }
        mCamera.setPreviewCallbackWithBuffer(this);
        for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++)
            mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void releaseCamera() {
        Log.e(TAG, "release camera");
        isInPause = true;

        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewTexture(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        isInPause = true;
    }

    @Override
    protected void onCameraChange() {
        if (isInPause) {
            return;
        }

        Log.e(TAG, "onCameraChange");

        releaseCamera();

        mCameraNV21Byte = null;
        mFrameId = 0;

        if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK, mCameraWidth, mCameraHeight);
        } else {
            openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, mCameraWidth, mCameraHeight);
        }

        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mGLRenderer.switchCameraSurfaceTexture();
                faceunity.fuItemSetParam(mEffectItem, "isAndroid", 1.0);
                faceunity.fuItemSetParam(mEffectItem, "rotationAngle", 360 - mCameraOrientation);
            }
        });
    }


    @Override
    protected void onStartRecording() {
        MiscUtil.Logger(TAG, "PRPR --> start recording", false);
        mIsRecording = true;
        mTextureMovieEncoder = new TextureMovieEncoder();
        SectionRecordTool.getInstance().setCurrentStartRecrodTimeMillis(System.currentTimeMillis());
    }

    @Override
    protected void onPauseRecording() {
        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(TextureMovieEncoder.IN_RECORDING)) {
            MiscUtil.Logger(TAG, "PRPR --> pause recording", false);
//            mTextureMovieEncoder.setRecordingStatus(TextureMovieEncoder.PAUSE_RECORDING);
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mTextureMovieEncoder.pauseRecording();
                }
            });


        }
    }

    @Override
    protected void onResumeRecording() {
        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(TextureMovieEncoder.PAUSE_RECORDING)) {
//            mTextureMovieEncoder.setRecordingStatus(TextureMovieEncoder.IN_RECORDING);
            MiscUtil.Logger(TAG, "PRPR --> resume recording", false);
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mTextureMovieEncoder.resumeRecording();
                }
            });
        }
    }

    @Override
    protected void onStopRecording() {
        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(TextureMovieEncoder.IN_RECORDING)) {
            MiscUtil.Logger(TAG, "stop recording", false);
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mTextureMovieEncoder.stopRecording();
                }
            });
            mIsRecording = false;
            long currentTotalRecordTimeMillis = SectionRecordTool.getInstance().getCurrentTotalRecordTimeMillis();
            SectionRecordTool.getInstance().addSectionIndex(currentTotalRecordTimeMillis);
            sectionProgressBar.addSection(currentTotalRecordTimeMillis);
        }
    }

    @Override
    protected void onBlurLevelSelected(int level) {
        mFaceBeautyBlurLevel = level;
    }

    @Override
    protected void onALLBlurLevelSelected(int isAll) {
        mFaceBeautyALLBlurLevel = isAll;
    }

    @Override
    protected void onCheekThinSelected(int progress, int max) {
        mFaceBeautyCheekThin = 1.0f * progress / max;
    }

    @Override
    protected void onColorLevelSelected(int progress, int max) {
        mFaceBeautyColorLevel = 1.0f * progress / max;
    }

    @Override
    protected void onEffectSelected(String effectItemName) {
        if (effectItemName.equals(mEffectFileName)) {
            return;
        }
        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);
        mEffectFileName = effectItemName;
        isNeedEffectItem = true;
    }

    @Override
    protected void onFilterLevelSelected(int progress, int max) {
        mFilterLevel = 1.0f * progress / max;
    }

    @Override
    protected void onEnlargeEyeSelected(int progress, int max) {
        mFaceBeautyEnlargeEye = 1.0f * progress / max;
    }

    @Override
    protected void onFilterSelected(String filterName) {
        mFilterName = filterName;
    }

    @Override
    protected void onRedLevelSelected(int progress, int max) {
        mFaceBeautyRedLevel = 1.0f * progress / max;
    }

    @Override
    protected void onFaceShapeLevelSelected(int progress, int max) {
        mFaceShapeLevel = (1.0f * progress) / max;
    }

    @Override
    protected void onFaceShapeSelected(int faceShape) {
        mFaceShape = faceShape;
    }

    private static final String strCalibrating = "表情校准中";
    private int showNum = 0;

    private Runnable mCalibratingRunnable = new Runnable() {

        @Override
        public void run() {
            showNum++;
            StringBuilder builder = new StringBuilder();
            builder.append(strCalibrating);
            for (int i = 0; i < showNum; i++) {
                builder.append(".");
            }
            isCalibratingText.setText(builder);
            if (showNum < 6) {
                isCalibratingText.postDelayed(mCalibratingRunnable, 500);
            } else {
                isCalibratingText.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onToggleFlash() {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }

            mCamera.setParameters(parameters);
        }
    }

    @Override
    protected void onToggleBeauty() {
        if (mBeauty) {
            mBeauty = false;
        } else {
            mBeauty = true;
        }
    }

    @Override
    protected void onChangeSpeed(@TextureMovieEncoder.Speed int speed) {
        mSpeed = speed;
        if (mTextureMovieEncoder != null) {
            mTextureMovieEncoder.setSpeed(mSpeed);
        }
    }

    @Override
    protected void onMusicClick() {
        Intent intent = new Intent(getContext(), MusicActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNextClick() {
        if (tmpVideoPathList != null && tmpVideoPathList.size() > 0) {
            String mergePath = VideoUtils.createOutputFile4Video(Constants.getVideoRecordPath(Objects.requireNonNull(XinApplicationUtil.Companion.getInstance().getMApplication())));
            VideoUtils.merge(tmpVideoPathList, mergePath);
            VideoUtils.deleteTmpVideo(tmpVideoPathList);

            sectionProgressBar.clear();

            Toast.makeText(getContext(), "合并成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), PreviewActivity.class);
            intent.putExtra(MERGE_PATH, mergePath);
            startActivity(intent);
        }
    }

    @Override
    protected void onDeleteClick() {
        Log.e(TAG, "onDeleteClick  tmpVideoPathList.size() = " + tmpVideoPathList.size());
        if (tmpVideoPathList.size() > 0) {
            tmpVideoPathList.remove(tmpVideoPathList.size() - 1);
            SectionRecordTool.getInstance().removeCurrentSectionIndex();
            sectionProgressBar.removeLastSection();
        }
    }
}
