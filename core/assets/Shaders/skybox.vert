#version 100
#ifdef GL_ES
#define MEDIUMP mediump
precision mediump float;
#else
#define MEDIUMP
#endif

attribute MEDIUMP vec4 a_position;

uniform MEDIUMP mat4 u_projTrans;

varying MEDIUMP float v_yCoord;

void main() {
	v_yCoord = a_position.y;
	gl_Position = u_projTrans * a_position;
}