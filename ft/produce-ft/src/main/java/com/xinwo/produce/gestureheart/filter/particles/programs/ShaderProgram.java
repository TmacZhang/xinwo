package com.xinwo.produce.gestureheart.filter.particles.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.xinwo.produce.gestureheart.glutils.ShaderHelper;
import com.xinwo.produce.gestureheart.glutils.TextResourceReader;


/**
 * Created by 25623 on 2017/12/14.
 */

public class ShaderProgram {
    //Uniform constants
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    //Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    protected static final String U_TIME = "u_Time";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICILE_START_TIME = "a_ParticleStartTime";

    protected final int program;

    public ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId){
        program = ShaderHelper.buildProgram(TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram(){
        GLES20.glUseProgram(program);
    }
}
