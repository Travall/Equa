#version 100 
#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D text;

varying vec2 texCoords;

void main()
{
	vec4 pix = texture2D(text, texCoords);
	if (pix.a <= 0.0) discard;
	gl_FragColor = pix;
}
