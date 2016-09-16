#version 120

uniform sampler2D tex;
uniform ivec2 texSize;

varying vec4 textColor;
varying vec4 shadowColor;
varying vec2 uv;

void main() {
	vec4 tex = texture2D(tex,uv/texSize);

	vec3 color = ( (textColor * tex.r) + (shadowColor * tex.g) ).rgb;

    float mainAlpha = tex.a * tex.r * textColor.a;
    float outlineAlpha = tex.a * tex.g * shadowColor.a * textColor.a;

	gl_FragColor = vec4(color, mainAlpha + outlineAlpha);
}
