package com.xjh.gestureheart.filter.particles.objects;

import android.graphics.Color;
import android.opengl.Matrix;


import com.xjh.gestureheart.filter.particles.util.Geometry;
import com.xinwo.xinview.history.ParticlePoint;

import java.util.Random;

/**
 * Created by 25623 on 2018/2/26.
 */

public class ParticleShooter {
    private final int color;

    private final float angleVariance;
    private final float speedVariance;

    private final Random random = new Random();

    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    public ParticleShooter(Geometry.Vector direction, int color,
                           float angleVarianceInDegrees, float speedVariance){
        this.color = color;
        this.angleVariance = angleVarianceInDegrees;
        this.speedVariance = speedVariance;

        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
    }

    /**
     *
     * @param particleSystem
     * @param point  粒子的起始位置
     * @param count     每次draw增加的粒子个数
     */
    public void addParticles(ParticleSystem particleSystem, ParticlePoint point, int count){
        for(int i=0; i<count; i++){
            Matrix.setRotateEulerM(rotationMatrix, 0,
                    (random.nextInt(72)) * angleVariance,
                    (random.nextInt(72)) * angleVariance,
                    (random.nextInt(72)) * angleVariance);

            Matrix.multiplyMV(resultVector, 0,
                    rotationMatrix, 0,
                    directionVector, 0);

            float speedAdjustment = 0.1f + random.nextFloat() * speedVariance / 10;

            //在 particle_vertex_shader.glsl 中用如下方式计算当前粒子位置（现在就相当于给位置加上了一个速度变量）
            //vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);
            Geometry.Vector thisDirection = new Geometry.Vector(
                    resultVector[0] * speedAdjustment / 4,
                    resultVector[1] * speedAdjustment / 4,
                    resultVector[2] * speedAdjustment * 4
            );

            //将这个粒子的方向和颜色存储在point中
            point.directionX = thisDirection.x;
            point.directionY = thisDirection.y;
            point.directionZ = thisDirection.z;

            point.r = Color.red(color) / 255f;
            point.g = Color.green(color) / 255f;
            point.b = Color.blue(color) / 255f;

            particleSystem.addParticle(point);
        }
    }
}
