#version 100
#ifdef GL_ES
#define LOWP lowp
#define MEDIUMP mediump
precision mediump float;
#else
#define LOWP
#define MEDIUMP
#endif

varying MEDIUMP float v_yCoord;

uniform LOWP vec4 u_sky;
uniform LOWP vec4 u_fog;

void main() {
	gl_FragColor = mix(u_fog, u_sky, clamp((v_yCoord*1.2)+0.5, 0.0, 1.0));
}