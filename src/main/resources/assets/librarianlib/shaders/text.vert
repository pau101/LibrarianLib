#version 120

attribute vec4 shadowColor;

uniform ivec2 texSize;
varying vec4 shadowColorVarying;

void main() {
    shadowColorVarying = shadowColor;
    gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
