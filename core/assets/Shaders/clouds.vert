#version 330
attribute vec4 a_position;

uniform mat4 u_projTrans;

in vec3 i_offset;
in float i_scale;

out float scale;

void main() {
    scale = i_scale;
    vec4 offsetPos = vec4(i_offset.x / 2.5,i_offset.y, i_offset.z / 2.5, 1.0);
    vec4 newPos = vec4( a_position.x * i_scale, a_position.y * i_scale, a_position.z * i_scale, 1 ) + offsetPos;
	gl_Position = u_projTrans * newPos;
}