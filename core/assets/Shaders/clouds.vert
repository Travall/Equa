#version 310 es
#ifdef GL_ES
    precision highp float;
#endif

in vec3 a_position;

in vec2 offset;
in float scale;

//out float yPos;

uniform mat4 projTrans;

void main() {
	//yPos = a_position.y;
	vec3 newPos = (a_position * scale) + vec3(offset.x, 200.0, offset.y);
	gl_Position = projTrans * vec4(newPos, 1.0);
}