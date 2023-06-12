#version 300 es
precision mediump float;

uniform vec3                   iResolution;
uniform float                  iTime;
uniform sampler2D              iChannel0;
in vec2                         vTexCoord;
out vec4                        gl_FragColor;

float average(vec3 col)
{
    return (col.r+col.g+col.b)/3.0;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy;

    vec2 uv2 = uv;
    uv2.x = 1.0-uv2.x;

	vec3 col1 = texture(iChannel0, uv).rgb;
    vec3 col2 = texture(iChannel0, uv2).rgb;

    if (average(col1)>average(col2))
    {
        fragColor = vec4(col1.r,col1.g,col1.b,1.0);
    }
    else
    {
        fragColor = vec4(col2.r,col2.g,col2.b,1.0);
    }

}

void main(){
    mainImage(gl_FragColor, vTexCoord);
}
