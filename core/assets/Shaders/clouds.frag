#version 310 es
#ifdef GL_ES
    precision lowp float;
#endif

//in float yPos;

out vec4 color;

void main() {
	//float a = 1.0 + yPos;
	color = vec4(1.0, 1.0, 1.0, 0.02);
}
