#version 300 es
precision mediump float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

float hash( float n )
{
    return fract(sin(n)*43758.5453);
}

float noise( in vec3 x )
{
    vec3 p = floor(x);
    vec3 f = fract(x);

    f = f*f*(3.0-2.0*f);

    float n = p.x + p.y*57.0 + 113.0*p.z;

    return mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
                        mix( hash(n+ 57.0), hash(n+ 58.0),f.x),f.y),
                    mix(mix( hash(n+113.0), hash(n+114.0),f.x),
                        mix( hash(n+170.0), hash(n+171.0),f.x),f.y),f.z);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy;

	float blurx = noise(vec3(iTime * 50.0, 0.0, 0.0)) * 2.0 - 1.0;
	float offsetx = blurx * 0.025;

	float blury = noise(vec3(iTime * 50.0, 1.0, 0.0)) * 2.0 - 1.0;
	float offsety = blury * 0.01;


	vec2 ruv = uv + vec2(offsetx, offsety);
	vec2 guv = uv + vec2(-offsetx, -offsety);
	vec2 buv = uv + vec2(0.00, 0.0);

	float r = texture(iChannel0, ruv).r;
	float g = texture(iChannel0, guv).g;
	float b = texture(iChannel0, buv).b;

	fragColor = vec4(r, g, b, 1.0);
}

void main(){
    mainImage(gl_FragColor, vTexCoord);
}
