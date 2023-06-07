#version 300 es
precision highp float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

float rand(float x){return fract(sin(x*1e4));}

float pattern(vec2 uv){
    return 1.+sin((uv.y+rand(uv.x+iTime)*.02)*100.+iTime*100.)*.2;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy;
    vec2 st=uv;
    st.x+=(rand(uv.y+iTime)-.5)*abs(rand(floor(iTime)))*.05;
    st.x=(st.x-.5)*1.2+.5;
    st.x+=step(.8,fract(iTime))*sin(iTime*20.+uv.y*10.)*.02;
    st.y=fract(uv.y+sin(floor(iTime)*1e4)*iTime);
    vec3 c=texture(iChannel0,st).rgb;
    c*=step(0.,st.x)-step(1.,st.x);
    vec2 offset = vec2(0.2,0.1);
    c.r*=pattern(uv+offset);
    c.g*=pattern(uv);
    c.b*=pattern(uv-offset);
    fragColor=vec4(c,1.);
}

void main() {
	mainImage(gl_FragColor, vTexCoord);
}
