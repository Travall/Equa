#version 100
#ifdef GL_ES
#define LOWP lowp
#define MEDIUMP mediump
precision mediump float;
#else
#define LOWP
#define MEDIUMP
#endif

uniform sampler2D u_texture;

varying LOWP float v_shade;
varying LOWP float v_light;
varying MEDIUMP vec2 v_texCoords;

const LOWP float gamma = 2.2;

void main()
{
	LOWP vec4 pix = texture2D(u_texture, v_texCoords);
	if (pix.a <= 0.0) discard; // Don't draw the transparent pixel.
	pix.rgb = (pix.rgb * v_shade) * pow(v_light, gamma);
	gl_FragColor = pix;
}
