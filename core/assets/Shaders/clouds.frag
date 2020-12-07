#version 100
#ifdef GL_ES
    precision lowp float;
#endif

in float scale;

void main() {
	gl_FragColor = vec4(1.0,1.0,1.0,0.1);
}
