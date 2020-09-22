#version 100
#ifdef GL_ES
    precision lowp float;
#endif

void main() {
	gl_FragColor = vec4(vec3(1.0), 0.4);
}
