#version 300 es
precision highp float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

#define t iTime
float C,S;


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy;
    float hs=step(.3,sin(t+2.+4.*sin(t*9.)))*(sin(uv.y*10.+t*5.)+sin(t)*sin(t*20.)*.5)*.05;
    uv.x+=hs;
    float vs=step(.8,sin(t+2.*sin(t*4.)))*(sin(t)*sin(t*20.)+cos(t)*sin(t*200.)*.1);
    uv.y=fract(uv.y+vs);
    vec3 c=texture(iChannel0,uv).rgb;
    c.xy *= mat2(C=cos(iTime*.3),S=sin(iTime*.3),-S,C);
    c.yz *= mat2(C=cos(iTime*.5),S=sin(iTime*.5),-S,C);
    c.xy *= mat2(C=cos(iTime*.7),S=sin(iTime*.7),-S,C);
    c.yz *= mat2(C=cos(iTime*.9),S=sin(iTime*.9),-S,C);
	fragColor = vec4(floor(5.*abs(c))*.2,1.0);
}

void main() {
	mainImage(gl_FragColor, vTexCoord);
}
