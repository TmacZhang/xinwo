package com.xjh.gestureheart.filter.particles.objects;


import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;

import com.xjh.gestureheart.filter.particles.data.VertexArray;
import com.xjh.gestureheart.filter.particles.programs.ParticleShaderProgram;
import com.xjh.gestureheart.filter.particles.util.Constants;
import com.xinwo.xinview.history.ParticlePoint;
import com.xinwo.xinview.history.ParticleStack;


/**
 * Created by 25623 on 2018/2/26.
 */

public class ParticleSystem {
    private final String TAG = "ParticleSystem";

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COUNT = 1;

    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT
            + VECTOR_COMPONENT_COUNT
            + PARTICLE_START_TIME_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;

    private float[] particles;  //数据存储顺序：x, y, z, r, g, b, directionX, directionY, directionZ, particlePointShowTime
    private final VertexArray vertexArray;
    private final int maxParticleCount;
    private int mMaxParticleCount;

    private int currentParticleCount;
    private int nextParticle;

    public ParticleSystem(int maxParticleCount){
        this.mMaxParticleCount = maxParticleCount;
        particles = new float[mMaxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(ParticlePoint point){
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;    //新粒子的起点
        int currentOffset = particleOffset;  //新粒子内部的偏移量
        nextParticle++;

        if(currentParticleCount < maxParticleCount){
            currentParticleCount++;
        }

        Log.e(TAG,"currentParticleCount = " + currentParticleCount + "  maxParticleCount = " + maxParticleCount);

        //达到数组结尾，重新从0开始
        if(nextParticle == maxParticleCount){
            nextParticle = 0;
        }

        Log.e(TAG,"x = " + point.x + "   , y = " + point.y + "    z = "+ point.z);

        //新粒子写入到数组中
        particles[currentOffset++] = point.x;
        particles[currentOffset++] = point.y;
        particles[currentOffset++] = point.z;

        //颜色的变化最终在particle_fragment_shader.glsl中完成

        particles[currentOffset++] = point.r;
        particles[currentOffset++] = point.g;
        particles[currentOffset++] = point.b;

        particles[currentOffset++] = point.directionX;
        particles[currentOffset++] = point.directionY;
        particles[currentOffset++] = point.directionZ;

        particles[currentOffset++] = point.particlePointShowTimestamp / 1_000_000f;

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);

    }

    public void bindData(ParticleShaderProgram particleProgram){
        int dataOffset = 0;

        //位置
        vertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        //颜色
        vertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        //方向：包括旋转角度和速度
        vertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getDirectionVectorLocation(),
                VECTOR_COMPONENT_COUNT,
                STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;

        //开始时间
        vertexArray.setVertexAttribPointer(dataOffset,
                particleProgram.getParticleStartTimeLocation(),
                PARTICLE_START_TIME_COUNT,
                STRIDE);
    }

    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount);
    }

    public void removeParticles(){
        Log.i(TAG, "removeParticles -----------");
        currentParticleCount = 0;
        particles = new float[mMaxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray.updateBuffer(particles, 0, particles.length/2);
    }

    /**
     * 将粒子更新到指定timestamp的状态
     * @param timestamp
     */
    public void updateParticles(long timestamp){
        int retrievedParticleStepIndex = ParticleStack.getInstance().indexOf(timestamp);
        if(retrievedParticleStepIndex > 0){
            //将particles倒序填充, 当完ParticleStack中的所有数据（i==0），或者将particles填满时(currentOffset==0)，即停止
            nextParticle = 0;
            int currentOffset = maxParticleCount * TOTAL_COMPONENT_COUNT;
            SparseArray<ParticlePoint> points;
            ParticlePoint point;
            int pointsSize;
            for(int i = retrievedParticleStepIndex - 1; i > 0 && currentOffset >= 0; --i){
                points = ParticleStack.getInstance().getPointsAt(i);
                pointsSize = points.size();
                for(int j = pointsSize-1; j >= 0; --j){
                    point = points.valueAt(j);

                    particles[--currentOffset] = point.particlePointShowTimestamp / 1_000_000f;

                    particles[--currentOffset] = point.directionZ;
                    particles[--currentOffset] = point.directionY;
                    particles[--currentOffset] = point.directionX;

                    particles[--currentOffset] = point.b;
                    particles[--currentOffset] = point.g;
                    particles[--currentOffset] = point.r;

                    particles[--currentOffset] = point.z;
                    particles[--currentOffset] = point.y;
                    particles[--currentOffset] = point.x;

                    if(currentOffset == 0){
                        break;
                    }
                }
            }

            for(int i=0; i<currentOffset; i++){
                particles[i] = 0;
            }

            vertexArray.updateBuffer(particles, 0, maxParticleCount * TOTAL_COMPONENT_COUNT);

        }else{
            Log.e(TAG,"更新失败，没有找到对应的 timestamp = " + timestamp);
        }

    }
}
