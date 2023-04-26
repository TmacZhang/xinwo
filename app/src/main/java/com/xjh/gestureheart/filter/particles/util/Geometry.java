package com.xjh.gestureheart.filter.particles.util;

/**
 * Created by 25623 on 2018/2/23.
 */

public class Geometry {

    /** 点 **/
    public static class Point{
        public float x, y, z;
        public float time;  // 秒

        public Point(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }


    public static class Vector{
        public final float x, y, z;

        public Vector(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
