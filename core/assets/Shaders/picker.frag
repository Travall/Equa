#version 100
#ifdef GL_ES
    precision lowp float;
#endif

uniform float u_alpha;

void main() {
	gl_FragColor = vec4(vec3(0.0), u_alpha);
}
