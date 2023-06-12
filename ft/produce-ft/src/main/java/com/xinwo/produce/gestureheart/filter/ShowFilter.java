package com.xinwo.produce.gestureheart.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.xinwo.produce.R;
import com.xinwo.produce.gestureheart.glutils.ShaderHelper;
import com.xinwo.produce.gestureheart.glutils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * Created by 25623 on 2018/3/19.
 */

public class ShowFilter {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int TEXUTRE_POSITION_COMPONENT_COUNT = 2;
    private static final int TEXUTRE_POSITION_STRIDE = 8;
    private static final int VERTEX_POSITION_COMPONENT_COUNT = 2;
    private static final int VERTEX_POSITION_STRIDE = 8;


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

    private final Context context;


    public ShowFilter(Context context) {
        this.context = context;

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

    public void onCreate() {

        programId = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.no_filter_vertex),
                TextResourceReader.readTextFileFromResource(context, R.raw.no_filter_fragment));


        aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION);
        aTexCoordLocation = GLES20.glGetAttribLocation(programId, A_TEXCOORD);
        iChannel0Location = GLES20.glGetUniformLocation(programId, I_CHANNEL0);

    }



    public void onDraw(int textureId) {
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


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    public void destory() {
        GLES20.glDeleteProgram(programId);
    }
}
