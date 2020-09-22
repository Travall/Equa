#version 100
#ifdef GL_ES
#define LOWP lowp
#define MEDIUM mediump
precision highp float;
#else
#define LOWP
#define MEDIUM
#endif

attribute vec4 a_position;
attribute LOWP vec4 a_data;
attribute MEDIUM vec2 a_texCoord;

uniform mat4 u_projTrans;
uniform LOWP float sunLightIntensity;

varying LOWP float v_shade;
varying LOWP float v_light;
varying MEDIUM vec2 v_texCoords;

const LOWP float brightness = 0.1;

// data[sideLight&Ambiant, source-light, skylight, unused]
void main()
{
	// v_light = (source-light * brightness) + (skylight * brightness) * sunLightIntensity;
	v_light = (a_data.y + (1.0 - a_data.y) * brightness) + (a_data.z + (1.0 - a_data.z) * brightness) * sunLightIntensity;
	v_light = clamp(v_light, 0.0, 1.0);
	v_shade = a_data.x;
	
	v_texCoords = a_texCoord;
	gl_Position = u_projTrans * a_position;
}
