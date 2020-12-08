#version 310 es
#ifdef GL_ES
    precision lowp float;
#endif

out vec4 color;

void main() {
	color = vec4(1.0,1.0,1.0,0.02);
}
