package com.xjh.gestureheart.mediacodec;

import android.graphics.SurfaceTexture;
import android.view.Surface;

/**
 * Holds state associated with a Surface used for MediaCodec decoder output.
 * <p>
 * The (width,height) constructor create a SurfaceTexture,
 * and then create a Surface for that SurfaceTexture.  The Surface can be passed to
 * MediaCodec.configure() to receive decoder output.  When a frame arrives, we latch the
 * texture with updateTexImage, then render the texture with GL to a pbuffer.
 * <p>
 * The no-arg constructor skips the GL preparation step and doesn't allocate a pbuffer.
 * Instead, it just creates the Surface and SurfaceTexture, and when a frame arrives
 * we just draw it on whatever surface is current.
 * <p>
 * By default, the Surface will be using a BufferQueue in asynchronous mode, so we
 * can potentially drop frames.
 */

public class OutputSurface implements SurfaceTexture.OnFrameAvailableListener {
    public final static String TAG = "OutputSurface";
    private VideoClipper mVideoCliper;
    private Surface mSurface;
    private Object mFrameSyncObject = new Object();     // guards mFrameAvailable
    private boolean mFrameAvailable = false;
    private VideoDrawer mDrawer;

    public OutputSurface(int videoWidth, int videoHeight, int rotation, VideoClipper videoClipper){
        mVideoCliper = videoClipper;

        mDrawer = new VideoDrawer(this);
        mDrawer.onSurfaceCreated();
        mDrawer.onSurfaceChanged(videoWidth, videoHeight);
        mDrawer.setVideoRotation(rotation);
        SurfaceTexture mSurfaceTexture = mDrawer.getSurfaceTexture();
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mSurface = new Surface(mSurfaceTexture);
    }

    public Surface getSurface(){
        return mSurface;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (mFrameSyncObject) {
            if (mFrameAvailable) {
                throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
            }
            mFrameAvailable = true;
            mFrameSyncObject.notifyAll();
        }
    }

    public void setFilter(int filterIndex) {
        mDrawer.changeFilter(filterIndex);
    }

    /**
     * Latches the next buffer into the texture.  Must be called from the thread that created
     * the OutputSurface object, after the onFrameAvailable callback has signaled that new
     * data is available.
     */
    public void awaitNewImage() {
        final int TIMEOUT_MS = 500;
        synchronized (mFrameSyncObject) {
            while (!mFrameAvailable) {//没有新的Frame时，进入循环，开始等待
                try {
                    // Wait for onFrameAvailable() to signal us.  Use a timeout to avoid
                    // stalling the test if it doesn't arrive.
                    mFrameSyncObject.wait(TIMEOUT_MS);
                    if (!mFrameAvailable) {
                        // TODO: if "spurious wakeup", continue while loop
                        throw new RuntimeException("Surface frame wait timed out");
                    }
                } catch (InterruptedException ie) {
                    // shouldn't happen
                    throw new RuntimeException(ie);
                }
            }
            //循环结束直接置为false的作用是，可以不经过drawImage()就去获取下一帧，这样就可以跳帧
            mFrameAvailable = false;
        }
    }


    public void drawImage(){
        mDrawer.onDrawFrame();
    }

    public void release(){
        if(mSurface != null){
            mSurface.release();
            mSurface = null;
        }

        if(mDrawer != null){
            mDrawer.release();
            mDrawer = null;
        }
    }


    public long getVideoTimeStamp() {
        return mVideoCliper.getVideoTimestamp();
    }
}
