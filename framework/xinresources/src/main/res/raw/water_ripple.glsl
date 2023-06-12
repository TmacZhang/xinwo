#version 300 es
precision highp float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

#define CORRECT_TEXTURE_SIZE 0
#define TEXTURE_DOWNSCALE 2.0

#define VIEW_HEIGHT 0
#define VIEW_NORMALS 0
#define CHEAP_NORMALS 0


#define CORRECT_TEXTURE_SIZE 0
#define TEXTURE_DOWNSCALE 2.0

#define VIEW_HEIGHT 0
#define VIEW_NORMALS 0
#define CHEAP_NORMALS 0

float rand(vec2 uv, float t) {
    float seed = dot(uv, vec2(12.3435, 25.3746));
    return fract(sin(seed) * 234536.3254 + t);
}

vec2 scale_uv(vec2 uv, vec2 scale, vec2 center) {
	return (uv - center) * scale + center;
}

vec2 scale_uv(vec2 uv, vec2 scale) {
    return scale_uv(uv, scale, vec2(0.5));
}

float create_ripple(vec2 coord, vec2 ripple_coord, float scale, float radius, float range, float height) {
	float dist = distance(coord, ripple_coord);
    return sin(dist / scale) * height * smoothstep(dist - range, dist + range, radius);
}

vec2 get_normals(vec2 coord, vec2 ripple_coord, float scale, float radius, float range, float height) {
    return vec2(
        create_ripple(coord + vec2(1.0, 0.0), ripple_coord, scale, radius, range, height) -
        create_ripple(coord - vec2(1.0, 0.0), ripple_coord, scale, radius, range, height),
        create_ripple(coord + vec2(0.0, 1.0), ripple_coord, scale, radius, range, height) -
        create_ripple(coord - vec2(0.0, 1.0), ripple_coord, scale, radius, range, height)
    ) * 0.5;
}

vec2 get_center(vec2 coord, float t) {
    t = round(t + 0.5);
    return vec2(
        sin(t - cos(t + 2354.2345) + 2345.3) * 0.5 + 0.5,
        sin(t + cos(t - 2452.2356) + 1234.0) * 0.5 + 0.5
    ) * iResolution.xy;
}

void mainImage(out vec4 color, in vec2 coord) {
    vec2 ps = vec2(1.0) / iResolution.xy;
    vec2 uv = coord;

    float timescale = 1.0;
    float t = fract(iTime * timescale);

    vec2 center =  get_center(coord * iResolution.xy, iTime * timescale);
    vec2 normals = get_normals(coord * iResolution.xy, center, t * 100.0 + 1.0, 100.0, 200.0, 1000.0);
    color = texture(iChannel0, uv + normals * ps);
}

void main() {
	mainImage(gl_FragColor, vTexCoord);
}
