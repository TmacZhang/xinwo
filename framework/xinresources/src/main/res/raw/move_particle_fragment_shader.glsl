#version 300 es
precision mediump float;
in vec3 v_Color;
in float v_ElapsedTime;

uniform sampler2D u_TextureUnit;

out vec4 gl_FragColor;

void main(){
    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0f) * texture(u_TextureUnit, gl_PointCoord);
}