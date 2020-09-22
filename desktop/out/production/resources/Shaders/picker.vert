#version 100
attribute vec4 a_position;

uniform mat4 u_projTrans;
uniform vec3 u_trans;

void main() {
	gl_Position = u_projTrans * (a_position + vec4(u_trans, 0.0));
}
