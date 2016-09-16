#version 120

attribute vec4 textColorIn;
attribute vec4 shadowColorIn;
attribute vec2 uvIn;

uniform ivec2 texSize;

varying vec4 shadowColor;
varying vec4 textColor;
varying vec2 uv;

void main() {
    shadowColor = shadowColorIn;
    textColor = textColorIn;
    uv = uvIn;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
