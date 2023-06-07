#version 300 es
precision highp float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

vec3 rainbow(float h) {
	h = mod(mod(h, 1.0) + 1.0, 1.0);
	float h6 = h * 6.0;
	float r = clamp(h6 - 4.0, 0.0, 1.0) +
		clamp(2.0 - h6, 0.0, 1.0);
	float g = h6 < 2.0
		? clamp(h6, 0.0, 1.0)
		: clamp(4.0 - h6, 0.0, 1.0);
	float b = h6 < 4.0
		? clamp(h6 - 2.0, 0.0, 1.0)
		: clamp(6.0 - h6, 0.0, 1.0);
	return vec3(r, g, b);
}

vec3 plasma(vec2 fragCoord)
{
	const float speed = 12.0;

	const float scale = 2.5;

	const float startA = 563.0 / 512.0;
	const float startB = 233.0 / 512.0;
	const float startC = 4325.0 / 512.0;
	const float startD = 312556.0 / 512.0;

	const float advanceA = 6.34 / 512.0 * 18.2 * speed;
	const float advanceB = 4.98 / 512.0 * 18.2 * speed;
	const float advanceC = 4.46 / 512.0 * 18.2 * speed;
	const float advanceD = 5.72 / 512.0 * 18.2 * speed;

	vec2 uv = fragCoord * scale;

	float a = startA + iTime * advanceA;
	float b = startB + iTime * advanceB;
	float c = startC + iTime * advanceC;
	float d = startD + iTime * advanceD;

	float n = sin(a + 3.0 * uv.x) +
		sin(b - 4.0 * uv.x) +
		sin(c + 2.0 * uv.y) +
		sin(d + 5.0 * uv.y);

	n = mod(((4.0 + n) / 4.0), 1.0);

	vec2 tuv = fragCoord.xy;
	n += texture(iChannel0, tuv).r;

	return rainbow(n);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec3 green = vec3(0.173, 0.5, 0.106);
	vec2 uv = fragCoord.xy ;
	vec3 britney = texture(iChannel0, uv).rgb;
	float greenness = 1.0 - (length(britney - green) / length(vec3(1, 1, 1)));
	float britneyAlpha = clamp((greenness - 0.7) / 0.2, 0.0, 1.0);
	fragColor = vec4(britney * (1.0 - britneyAlpha), 1.0) + vec4(plasma(fragCoord) * britneyAlpha, 1.0);
}

void main() {
    mainImage(gl_FragColor, vTexCoord);
}
