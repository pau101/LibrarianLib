#version 120

uniform sampler2D tex;

varying vec4 shadowColorVarying;

void main() {
	vec4 color = texture2D(tex,gl_TexCoord[0].st);
	gl_FragColor = color.r * gl_Color + color.g * shadowColorVarying;
}
