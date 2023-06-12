package com.xinwo.produce.gestureheart.gl;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;

//import static android.opengl.EGLExt.EGL_RECORDABLE_ANDROID;

/**
 * Created by 25623 on 2018/3/12.
 */

public class EGLCore {
    private final static String TAG = "EGLCore";

    /**
     * Constructor flag: surface must be recordable.  This discourages EGL from using a
     * pixel format that cannot be converted efficiently to something usable by the video
     * encoder.
     *
     */
    public static final int FLAG_RECORDABLE = 0x01;

    public static final int EGL_VERSION2 = 2;
    public static final int EGL_VERSION3 = 3;


    private EGLDisplay mEGLDisplay;
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext;

    public EGLCore(@Nullable EGLContext shareContext, int flags, int eglVersion){
        Log.e(TAG, "EGLCore 构造函数  mEGLDisplay = " + mEGLDisplay + "     EGL14.EGL_NO_DISPLAY = " + EGL14.EGL_NO_DISPLAY);

        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if(mEGLDisplay == EGL14.EGL_NO_DISPLAY){
            throw new RuntimeException("EGL无法获取Display");
        }

        int[] versions = new int[2];// 存储两个版本号：大版本，小版本
        boolean initialize = EGL14.eglInitialize(mEGLDisplay, versions, 0, versions, 1);

        if(!initialize){
            Log.e(TAG,"EGL 初始化失败");
            int error = EGL14.eglGetError();
            if(error == EGL14.EGL_BAD_DISPLAY){
                throw new RuntimeException("EGL 初始化未设置有效的Display");
            }else if(error == EGL14.EGL_NOT_INITIALIZED){
                throw new RuntimeException("EGL 不能初始化");
            }
        }

        mEGLConfig = getConfig(flags, eglVersion);
        mEGLContext = createEGLContext(shareContext, eglVersion);
    }

    private EGLConfig getConfig(int flags, int eglVersion) {
        int renderableType = EGL14.EGL_OPENGL_ES2_BIT;
        if(eglVersion == EGL_VERSION3){
            renderableType |= EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        }

        // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
        // doesn't really help.  It can also lead to a huge performance hit on glReadPixels()
        // when reading into a GL_RGBA buffer.
        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                //EGL14.EGL_DEPTH_SIZE, 16,
                //EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, renderableType,
                EGL14.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL14.EGL_NONE
        };
        if ((flags & FLAG_RECORDABLE) != 0) {
//            attribList[attribList.length - 3] = EGL_RECORDABLE_ANDROID;
            attribList[attribList.length - 3] = 1;
            attribList[attribList.length - 2] = 1;
        }
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];

        /**
         EGL14.eglChooseConfig()查询每个表面配置，找出最好的选择。
         EGL14.eglGetConfigs()是指定一组需求，让EGL推荐最佳匹配 (应该）
         */

        if (!EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                numConfigs, 0)) {
            Log.w(TAG, "unable to find RGB8888 / " + eglVersion + " EGLConfig");
            return null;
        }
        return configs[0];
    }

    /**
     * 创建一个与Android上的Surface相关联的 EGL surface，
     * 如果是给MediaCodec使用，则创建EGLSurface的EGLConfig应该具有recordable属性
     * @param surface
     * @return
     */
    public EGLSurface createWindowSurface(Object surface){
        if( !(surface instanceof Surface) && !(surface instanceof SurfaceTexture)){
            throw new RuntimeException("无效的suface: "+ surface);
        }

        int[] surfaceAttribs = new int[]{
                EGL14.EGL_NONE
        };

        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(mEGLDisplay,
                mEGLConfig,
                surface,
                surfaceAttribs,
                0);

        int error = EGL14.eglGetError();
        if(error != EGL14.EGL_SUCCESS){
            String errorMsg = null;
            switch(error){
                case EGL14.EGL_BAD_MATCH:
                    errorMsg = "原生窗口不匹配提供的EGLConfig " +
                            "或者 提供的EGLConfig不支持渲染到窗口" +
                            "（也就是EGL_SURFACE_TYPE属性没有设置EGL_WINDOW_BIT）";//ES3.0 提供的错误信息
                    break;
                case EGL14.EGL_BAD_CONFIG:
                    errorMsg = "系统不支持所提供的EGLConfig";
                    break;
                case EGL14.EGL_BAD_NATIVE_WINDOW:
                    errorMsg = "提供的原生窗口（surface）句柄无效";
                    break;
                case EGL14.EGL_BAD_ALLOC:
                    errorMsg = "eglCreateWindowSurface无法为新的EGL窗口分配资源 " +
                            "或者 已经有和所提供的原生窗口相关联的EGLConfig";
                    break;
            }

            throw new RuntimeException(errorMsg);
        }
        return eglSurface;
    }


    /**
     * 一个应用程序可能创建多个EGLContext用于不同用途，所以我们需要关联特定的EGLContext与EGLSurface.
     *  “写”与“读”使用同一个eglSurface
     * @param eglSurface
     */
    public void makeCurrent(EGLSurface eglSurface){
        if(!EGL14.eglMakeCurrent(mEGLDisplay,eglSurface,eglSurface,mEGLContext)){
            throw new RuntimeException("eglMakeCurrent(draw,read) failed");
        }
    }

    public boolean swrap(EGLSurface eglSurface){
       return EGL14.eglSwapBuffers(mEGLDisplay,eglSurface);
    }

    /**
     * 必需有一个上下文才能绘制
     * @return
     * @param shareContext
     */
    private EGLContext createEGLContext(EGLContext shareContext, int eglVersion){

        if(shareContext == null){
            shareContext = EGL14.EGL_NO_CONTEXT;
        }

        int[] contextAtrribs = new int[]{
            EGL14.EGL_CONTEXT_CLIENT_VERSION, eglVersion,
                EGL14.EGL_NONE
        };

        EGLContext eglContext = EGL14.eglCreateContext(mEGLDisplay,
                mEGLConfig,
                shareContext,                //EGL14.EGL_NO_CONTEXT,   // 是否与其他上下文共享资源
                contextAtrribs,
                0);
        if(EGL14.EGL_NO_CONTEXT == eglContext){
            int error = EGL14.eglGetError();
            if(EGL14.EGL_BAD_CONTEXT == error){
                Log.e(TAG,"创建 EGLContext 失败");
            }
        }
        return eglContext;
    }


    /**
     * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
     */
    public void setPresentationTime(EGLSurface eglSurface, long nsecs) {
        EGLExt.eglPresentationTimeANDROID(mEGLDisplay, eglSurface, nsecs);
    }

    /**
     * Discards all resources held by this class, notably the EGL context.  This must be
     * called from the thread where the context was created.
     * <p>
     * On completion, no context will be current.
     */
    public void release() {
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.  So for
            // every eglInitialize() we need an eglTerminate().
            EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(mEGLDisplay);
        }

        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
        mEGLConfig = null;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                // We're limited here -- finalizers don't run on the thread that holds
                // the EGL state, so if a surface or context is still current on another
                // thread we can't fully release it here.  Exceptions thrown from here
                // are quietly discarded.  Complain in the log file.
                Log.w(TAG, "WARNING: EglCore was not explicitly released -- state may be leaked");
                release();
            }
        } finally {
            super.finalize();
        }
    }




    /**
     * Destroys the specified surface.  Note the EGLSurface won't actually be destroyed if it's
     * still current in a context.
     *
     */
    public void releaseSurface(EGLSurface eglSurface) {
        EGL14.eglDestroySurface(mEGLDisplay, eglSurface);
    }
}
