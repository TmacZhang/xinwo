package com.xinwo.produce.gestureheart.filter.particles.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.xinwo.produce.R;


/**
 * Created by 25623 on 2018/2/26.
 */

public class ParticleShaderProgram extends ShaderProgram {
    //Uniform locations
    private final int uTimeLocation;
    private final int uTextureUnitLocation;

    //Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;

    public ParticleShaderProgram(Context context){
        super(context, R.raw.move_particle_vertex_shader, R.raw.move_particle_fragment_shader);

        uTimeLocation = GLES20.glGetUniformLocation(program, U_TIME);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = GLES20.glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = GLES20.glGetAttribLocation(program, A_PARTICILE_START_TIME);
    }

    public void setUniforms(float elapsedTime, int textureId){
        GLES20.glUniform1f(uTimeLocation, elapsedTime);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getColorAttributeLocation(){
        return aColorLocation;
    }

    public int getDirectionVectorLocation(){
        return aDirectionVectorLocation;
    }


    public int getParticleStartTimeLocation(){
        return aParticleStartTimeLocation;
    }
}
