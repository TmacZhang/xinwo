#version 300 es

in vec4 aPosition;//顶点位置
in vec4 aTexCoord;// S T 纹理坐标
out vec2 vTexCoord;

 void main() {
     vTexCoord = aTexCoord.xy;
     gl_Position = aPosition;
 }