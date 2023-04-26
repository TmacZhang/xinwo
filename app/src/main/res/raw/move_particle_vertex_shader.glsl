#version 300 es
uniform mat4 u_Matrix;
uniform float u_Time;

in vec3 a_Position;
in vec3 a_Color;
in vec3 a_DirectionVector;
in float a_ParticleStartTime;

out vec3 v_Color;
out float v_ElapsedTime;

void main(){
    v_Color = a_Color;
    v_ElapsedTime = u_Time - a_ParticleStartTime;
    if(v_ElapsedTime >= 0.0f){
                vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);
                float gravityFactor = v_ElapsedTime * v_ElapsedTime / 4.0f; //模拟重力加速度，除以2是为了弱化效果
                currentPosition.z = currentPosition.z - gravityFactor - abs(currentPosition.x) - abs(currentPosition.y); //给y方向加上重力加速度因素
            //    gl_Position = u_Matrix * vec4(currentPosition, 1.0f);
                gl_Position = vec4(currentPosition, 1.0f);
                gl_PointSize = 25.0 + currentPosition.z * 10.0f - abs(currentPosition.x)*10.0f - abs(currentPosition.y)*10.0f;
    }else{
        gl_PointSize = 0.0f;
    }

}
