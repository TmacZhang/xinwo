#version 300 es
precision mediump float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{	
	float stongth = 0.3;
	vec2 uv = fragCoord.xy;
	float waveu = sin((uv.y + iTime) * 20.0) * 0.5 * 0.05 * stongth;
	fragColor = texture(iChannel0, uv + vec2(waveu, 0));
}

void main() {
	mainImage(gl_FragColor, vTexCoord);
}