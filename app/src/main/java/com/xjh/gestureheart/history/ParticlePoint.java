package com.xjh.gestureheart.history;

/**
 * Created by 25623 on 2018/4/9.
 */

public class ParticlePoint {
    public int particleFilterIndex;
    public float x, y, z;   //粒子位置
    public long particlePointShowTimestamp;  //粒子出现的Timestamp。 time = timestamp / 1_000_000f;
    public int step;    //编辑的步骤
    public float r, g, b;   //粒子颜色
    public float directionX, directionY, directionZ; //粒子方向
    public long particleStepStartTimestamp;    //粒子步骤开始时间

    public ParticlePoint(){}

    public ParticlePoint(int particleFilterIndex, float x, float y, float z, long particlePointShowTimestamp, int step){
        this.particleFilterIndex = particleFilterIndex;
        this.x = x;
        this.y = y;
        this.particlePointShowTimestamp = particlePointShowTimestamp;
        this.step = step;
    }

    @Override
    public String toString() {
        return "(index:" + particleFilterIndex + ", x:" + x + ", y:" + y + ", z:" + z + ", stamp:" + particlePointShowTimestamp + ", step:" + step + ")";
    }
}
