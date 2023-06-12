#version 300 es
precision mediump float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

float perspective = 0.3;

const int samples = 25;
const float minBlur = .1;
const float maxBlur = .3;
const float speed = 3.;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 p = fragCoord.xy;

    vec4 result = vec4(0);

    float timeQ = mix(minBlur, maxBlur, (sin(iTime*speed)+1.)/2.);

	for (int i=0; i<=samples; i++)
    {
        float q = float(i)/float(samples);
        result += texture(iChannel0, p + (vec2(0.5)-p)*q*timeQ)/float(samples);
    }


	fragColor = result;
}

void main(){
    mainImage(gl_FragColor, vTexCoord);
}
