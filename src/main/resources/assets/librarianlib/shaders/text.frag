#version 120

uniform sampler2D tex;

varying vec4 textColorVarying;
varying vec4 shadowColorVarying;

void main() {
	vec4 tex = texture2D(tex,gl_TexCoord[0].st);

	vec3 color = ( (textColorVarying * tex.r) + (shadowColorVarying * tex.g) ).rgb;

	// make the main alpha more pronounced, makes small text sharper
    tex.r = clamp(tex.r * 2.0, 0.0, 1.0);

    // alpha is the sum of main alpha and outline alpha
    // main alpha is main font color alpha
    // outline alpha is the stroke or shadow alpha
    float mainAlpha = tex.r * textColorVarying.a;
    float outlineAlpha = tex.g * shadowColorVarying.a * textColorVarying.a;

	gl_FragColor = vec4(color, mainAlpha + outlineAlpha);
}
