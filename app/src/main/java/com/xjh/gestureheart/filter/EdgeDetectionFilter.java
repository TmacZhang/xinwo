package com.xjh.gestureheart.filter;

import android.content.Context;
import android.opengl.GLES20;


import com.xjh.gestureheart.glutils.RenderBuffer;
import com.xjh.gestureheart.glutils.ShaderHelper;
import com.xjh.gestureheart.glutils.TextResourceReader;
import com.xjh.xinwo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by 25623 on 2018/3/9.
 */

public class EdgeDetectionFilter extends BaseFilter {

    //VertexShader
    private final String A_POSITION = "aPosition";
    private final String A_TEXCOORD = "aTexCoord";

    //FragmentShader
    private final String I_CHANNEL0 = "iChannel0";

    //顶点坐标
    private final float vertexData[] = {
            -1.0f,  1.0f,
            -1.0f,  -1.0f,
            1.0f,   1.0f,
            1.0f,   -1.0f
    };

    //纹理坐标ST, T不需要反转（已在BaseFilter旋转）
    private float[] textureData={
            0f, 1f,
            0f, 0f,
            1f, 1f,
            1f, 0f
    };

    private int programId;

    private int aPositionLocation;
    private int aTexCoordLocation;
    private int iChannel0Location;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    private RenderBuffer mRenderBuffer;

    public EdgeDetectionFilter(Context context) {
        super(context);

        mVertexBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        mVertexBuffer.position(0);

        mTextureBuffer = ByteBuffer
                .allocateDirect(textureData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        mTextureBuffer.position(0);

    }

    @Override
    protected void onCreate() {
        programId = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.no_filter_vertex),
                TextResourceReader.readTextFileFromResource(context, R.raw.edge_detection));



        aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION);
        aTexCoordLocation = GLES20.glGetAttribLocation(programId, A_TEXCOORD);
        iChannel0Location = GLES20.glGetUniformLocation(programId, I_CHANNEL0);
    }

    @Override
    protected void onChange(int surfaceWidth, int surfaceHeight) {

    }

    @Override
    protected void onDraw(int textureId, int surfaceWidth, int surfaceHeight) {
        if (mRenderBuffer == null ||
                mRenderBuffer.getWidth() != mSurfaceWidth ||
                mRenderBuffer.getHeight() != mSurfaceHeight) {
            mRenderBuffer = new RenderBuffer(mSurfaceWidth, mSurfaceHeight, RENDER_BUFFER_ACTIVE_TEXUTER_UNIT);
        }

        GLES20.glUseProgram(programId);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
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

        //Render to texture, 渲染到mRenderBuffer新建的texture中，目的是为了可以向下传递，添加新的纹理
        mRenderBuffer.bind();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        mRenderBuffer.unbind();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public int getTextureId() {
        return mRenderBuffer.getTexId();
    }
}
