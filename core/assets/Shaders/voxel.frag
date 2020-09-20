#version 100
#ifdef GL_ES
#define LOWP lowp
#define MEDIUM mediump
precision mediump float;
#else
#define LOWP
#define MEDIUM
#endif

uniform sampler2D u_texture;

varying LOWP float v_shade;
varying LOWP float v_light;
varying MEDIUM vec2 v_texCoords;

void main()
{
	LOWP vec4 pix = texture2D(u_texture, v_texCoords);
	if (pix.a <= 0.0) discard; // Don't draw the transparent pixel.
	gl_FragColor = (pix * v_shade) * v_light;
}
