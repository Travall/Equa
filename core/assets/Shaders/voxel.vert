#version 100
#ifdef GL_ES
#define LOWP lowp
#define MEDIUMP mediump
precision highp float;
#else
#define LOWP
#define MEDIUMP
#endif

attribute vec4 a_position;
attribute LOWP vec4 a_data;
attribute MEDIUMP vec2 a_texCoord;

uniform mat4 u_projTrans;
uniform LOWP float sunLightIntensity;
uniform LOWP float brightness;
uniform int toggleAO;

varying LOWP float v_shade;
varying LOWP float v_light;
varying MEDIUMP vec2 v_texCoords;

// data[sideLight&Ambiant, source-light, skylight, unused]
void main()
{
	v_light = min(mix(a_data.y + a_data.z * sunLightIntensity, 1.0, brightness), 1.0);
	v_shade = toggleAO == 1 ? a_data.x : 1.0;
	v_texCoords = a_texCoord;
	gl_Position = u_projTrans * a_position;
}
