package com.xjh.gestureheart.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;


import com.xjh.gestureheart.glutils.RenderBuffer;
import com.xjh.gestureheart.glutils.ShaderHelper;
import com.xjh.gestureheart.glutils.TextResourceReader;
import com.xjh.xinwo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by 25623 on 2018/3/7.
 */

public abstract class BaseFilter {
    protected final int BYTES_PER_FLOAT = 4;
    protected static final int RENDER_BUFFER_ACTIVE_TEXUTER_UNIT = GLES20.GL_TEXTURE8;

    //VertexShader
    private final String A_POSITION = "aPosition";
    private final String A_TEXCOORD = "aTexCoord";

    //FragmentShader
    private final String I_CHANNEL0 = "iChannel0";

    protected static final int VERTEX_POSITION_COMPONENT_COUNT = 2;
    protected static final int VERTEX_POSITION_STRIDE = 8;
    protected static final int TEXUTRE_POSITION_COMPONENT_COUNT = 2;
    protected static final int TEXUTRE_POSITION_STRIDE = 8;

    //顶点坐标
    private final float vertexData[] = {
            -1.0f,  1.0f,
            -1.0f,  -1.0f,
            1.0f,   1.0f,
            1.0f,   -1.0f
    };

    //纹理坐标ST, 其中T需要做反转
    private float[] textureData={
            0f, 0f,
            0f, 1f,
            1f, 0f,
            1f, 1f
    };


    protected Context context;
    protected int mSurfaceWidth;
    protected int mSurfaceHeight;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private int mProgram;
    private int aPositionLocation;
    private int aTexCoordLocation;
    private int iChannel0Location;


    private RenderBuffer mRenderBuffer;


    public BaseFilter(Context context){
        this.context = context;

        mVertexBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        mVertexBuffer.position(0);

        rotateTextureData(0);
        mTextureBuffer = ByteBuffer
                .allocateDirect(textureData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        mTextureBuffer.position(0);
    }

    public void create() {
        mProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.oes_base_vertex),
                TextResourceReader.readTextFileFromResource(context, R.raw.oes_base_fragment));

        aPositionLocation = GLES20.glGetAttribLocation(mProgram, A_POSITION);
        aTexCoordLocation = GLES20.glGetAttribLocation(mProgram, A_TEXCOORD);
        iChannel0Location = GLES20.glGetUniformLocation(mProgram, I_CHANNEL0);

        onCreate();
    }


    public void change(int surfaceWidth, int surfaceHeight){
        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;
        onChange(mSurfaceWidth, mSurfaceHeight);
    }



    public void draw(int textureId){
        if (mRenderBuffer == null ||
                mRenderBuffer.getWidth() != mSurfaceWidth ||
                mRenderBuffer.getHeight() != mSurfaceHeight) {
            mRenderBuffer = new RenderBuffer(mSurfaceWidth, mSurfaceHeight, RENDER_BUFFER_ACTIVE_TEXUTER_UNIT);
        }

        GLES20.glUseProgram(mProgram);

        //此处的textureId为 base_fragment_shader.glsl 中的 uTexture
        /**
         * 激活纹理单元，以便后续的glBindTexture绑定
         */
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(iChannel0Location, 0);

        mVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation,
                VERTEX_POSITION_COMPONENT_COUNT,
                GLES20.GL_FLOAT,
                false,
                VERTEX_POSITION_STRIDE,
                mVertexBuffer);

        mTextureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation);
        GLES20.glVertexAttribPointer(aTexCoordLocation,
                TEXUTRE_POSITION_COMPONENT_COUNT,
                GLES20.GL_FLOAT,
                false,
                TEXUTRE_POSITION_STRIDE,
                mTextureBuffer);



//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //Render to texture, 渲染到mRenderBuffer新建的texture中，目的是为了可以向下传递，添加新的纹理
        /**
         * 默认的帧缓冲区由系统窗口提供，
         * 现在我们调用 GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId); 来将帧缓冲区切换为我们自己创建的帧缓冲区 frameBufferId
         * 此时通过GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);绘制的纹理将会输出到我们自己创建的帧缓冲区frameBufferId
         * 而这个帧缓冲区又绑定了一个纹理，最终glDrawArrays绘制输出的数据会传输到frameBufferId绑定的纹理对象上
         */
        mRenderBuffer.bind();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        mRenderBuffer.unbind();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        onDraw(mRenderBuffer.getTexId(), mRenderBuffer.getWidth(), mRenderBuffer.getHeight());
    }



    private void rotateTextureData(int angle){// 旋转texture需要顺时针旋转
        switch (angle){
            case 0:
                textureData = new float[]{
                        0f, 0f,
                        0f, 1f,
                        1f, 0f,
                        1f, 1f
                };
                break;
            case 90:
                textureData = new float[]{
                        0f, 1f,
                        1f, 1f,
                        0f, 0f,
                        1f, 0f
                };
                break;
            case 180:
                textureData = new float[]{
                        1f, 1f,
                        1f, 0f,
                        0f, 1f,
                        0f, 0f
                };
                break;
            case 270:
                textureData = new float[]{
                        1f, 0f,
                        0f, 0f,
                        1f, 1f,
                        0f, 1f
                };
                break;
        }
    }

    public void setRotation(int rotation) {
        rotateTextureData(rotation);
        mTextureBuffer.clear();
        mTextureBuffer.put(textureData);
        mTextureBuffer.position(0);
    }

    public void destory(){
        //TODO 子类的还没有删除
        GLES20.glDeleteProgram(mProgram);
    }

    protected abstract void onCreate();

    protected abstract void onChange(int surfaceWidth, int surfaceHeight);

    protected abstract void onDraw(int textureId, int surfaceWidth, int surfaceHeight);

    public abstract int getTextureId();


}
