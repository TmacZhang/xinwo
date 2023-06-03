#version 300 es

precision mediump float;

uniform vec3                   iResolution;
uniform sampler2D              iChannel0;
uniform sampler2D              iChannel1;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

vec4 cell(vec2 fragCoord, vec2 pixel)
{
	vec2 uv = (fragCoord-pixel) / iResolution.xy;
    return texture(iChannel0, uv);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord / iResolution.xy;


    // get adjacents cells from backbuffer
    vec4 l = cell(fragCoord, vec2(-1,0)); // left cell
    vec4 r = cell(fragCoord, vec2(1,0)); // rigt cell
    vec4 t = cell(fragCoord, vec2(0,1)); // top cell
    vec4 b = cell(fragCoord, vec2(0,-1)); // bottom cell

    // get current cell from backbuffer
    vec4 c = cell(fragCoord, vec2(0,0)); // central cell

    // quad dist from cells
    fragColor = max(c, max(l,max(r,max(t,b))));

    // video merge
    fragColor = fragColor * .95 + texture(iChannel1, uv) * .05;

}

void main(){
    mainImage(gl_FragColor, vTexCoord * iResolution.xy);
}
