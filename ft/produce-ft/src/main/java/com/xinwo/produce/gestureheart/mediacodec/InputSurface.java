package com.xinwo.produce.gestureheart.mediacodec;

import android.opengl.EGLSurface;
import android.view.Surface;

import com.xinwo.produce.gestureheart.gl.EGLCore;

/**
 *
 * Holds state associated with a Surface used for MediaCodec encoder input.
 * <p>
 * The constructor takes a Surface obtained from MediaCodec.createInputSurface(), and uses that
 * to create an EGL window surface.  Calls to eglSwapBuffers() cause a frame of data to be sent
 * to the video encoder.
 */
public class InputSurface {
    private static final String TAG = "InputSurface";
    private Surface mSurface;
    private final EGLSurface mEGLSurface;
    private final EGLCore mEGLCore;

    public InputSurface(Surface surface){
        if (surface == null) {
            throw new NullPointerException();
        }
        mSurface = surface;

        mEGLCore = new EGLCore(null, EGLCore.FLAG_RECORDABLE, EGLCore.EGL_VERSION3);
        mEGLSurface = mEGLCore.createWindowSurface(mSurface);
    }

    public void makeCurrent(){
        mEGLCore.makeCurrent(mEGLSurface);
    }

    public boolean swapBuffers(){
        return mEGLCore.swrap(mEGLSurface);
    }

    public void setPresentationTime(long nsecs){
        mEGLCore.setPresentationTime(mEGLSurface, nsecs);
    }

    public void release(){
        mEGLCore.release();
        if(mSurface != null){
            mSurface.release();
            mSurface = null;
        }
    }

}
