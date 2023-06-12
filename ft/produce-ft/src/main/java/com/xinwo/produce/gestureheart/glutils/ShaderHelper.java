package com.xinwo.produce.gestureheart.glutils;

import android.opengl.GLES20;
import android.util.Log;


/**
 * Created by 25623 on 2017/12/3.
 */

public class ShaderHelper {
    private final static String TAG = "ShaderHelper";

    public static int compileShader(int type, String shaderCode){
        int shaderObjectId = GLES20.glCreateShader(type);

        if(shaderObjectId == 0){
            Log.e(TAG, "Could not create new shader");
            return 0;
        }

        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);

        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if(compileStatus[0] == 0){
            Log.e(TAG, "Comiple shader error:\n"+shaderCode+"\n"+ GLES20.glGetShaderInfoLog(shaderObjectId));
            GLES20.glDeleteShader(shaderObjectId);
            return 0;
        }

        return shaderObjectId;
    }

    private static int compileVertextShader(String shaderCode){
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    private static int compileFragmentShader(String shaderCode){
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId){
        int programObjectId = GLES20.glCreateProgram();

        if(programObjectId == 0){
            Log.e(TAG,"Can not create progream");
            return 0;
        }

        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);
        GLES20.glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if(linkStatus[0] == 0){
            Log.e(TAG,"Could not link program: "+ GLES20.glGetProgramInfoLog(programObjectId));
            GLES20.glDeleteProgram(programObjectId);
            return 0;
        }

        return programObjectId;
    }

    public static boolean validateProgream(int programObjectId){
        GLES20.glValidateProgram(programObjectId);

        final int [] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);

        Log.v(TAG, "Results of validating program: "+ GLES20.glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource){
        int program;

        int vertexShader = compileVertextShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        program = linkProgram(vertexShader, fragmentShader);

        validateProgream(program);

        return program;
    }
}
