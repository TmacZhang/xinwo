#version 300 es

precision mediump float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

void main() {
    gl_FragColor = texture(iChannel0, vTexCoord);
}
