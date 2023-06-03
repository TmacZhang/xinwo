#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require

precision mediump float;
in vec2 vTexCoord;
uniform samplerExternalOES iChannel0;    //视频的第一次使用需要samplerExternalOES将YUV转换为RGBA
out vec4 gl_FragColor;
void main() {
    gl_FragColor = texture(iChannel0, vTexCoord);
}
