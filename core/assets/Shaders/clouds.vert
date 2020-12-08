#version 310 es
#ifdef GL_ES
    precision highp float;
#endif

in vec4 a_position;

in vec2 i_offset;
in float i_scale;

uniform mat4 u_projTrans;

void main() {
    vec4 offsetPos = vec4(i_offset.x / 2.5, 300, i_offset.y / 2.5, 1.0);
    vec4 newPos = vec4( a_position.x * i_scale, a_position.y * i_scale, a_position.z * i_scale, 1 ) + offsetPos;
	gl_Position = u_projTrans * newPos;
}