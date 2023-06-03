#version 300 es
#extension GL_OES_standard_derivatives : enable
precision mediump float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

void mainImage( out vec4 fColor, in vec2 fragCoord )
{
    vec4 color =  texture(iChannel0, fragCoord);
    float gray = length(color.rgb);
    fColor = vec4(vec3(step(0.06, length(vec2(dFdx(gray), dFdy(gray))))), 1.0);
}

void main() {
        mainImage(gl_FragColor, vTexCoord);
}