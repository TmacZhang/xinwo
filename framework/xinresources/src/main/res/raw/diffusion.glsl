#version 300 es
precision mediump float;

uniform vec3                   iResolution;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;
	fragColor = texture(iChannel0, uv);
}

void main(){
    mainImage(gl_FragColor, vTexCoord * iResolution.xy);
}
