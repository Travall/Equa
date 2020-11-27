#version 310 es
#ifdef GL_ES
#define LOWP lowp
#define MEDIUMP mediump
precision mediump float;
#else
#define LOWP
#define MEDIUMP
#endif

uniform sampler2D text;

in LOWP float shade;
in LOWP float light;
in MEDIUMP vec2 texCoords;

out LOWP vec4 color;

const LOWP float gamma = 2.2;

void main()
{
	LOWP vec4 pix = texture(text, texCoords);
	if (pix.a <= 0.0) discard; // Don't draw the transparent pixel.
	pix.rgb = (pix.rgb * shade) * pow(light, gamma);
	color = pix;
}
