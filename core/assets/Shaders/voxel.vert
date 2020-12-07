#version 310 es
#ifdef GL_ES
#define LOWP lowp
#define MEDIUMP mediump
#define HIGHP highp
precision highp float;
#else
#define LOWP
#define MEDIUMP
#define HIGHP
#endif

in HIGHP vec4 position;
in LOWP vec4 data;
in MEDIUMP vec2 texCoord;

out LOWP float shade;
out LOWP float light;
out MEDIUMP vec2 texCoords;

uniform HIGHP mat4 projTrans;
uniform LOWP float sunLightIntensity;
uniform LOWP float brightness;
uniform int toggleAO;

// data[sideLight&Ambiant, source-light, skylight, unused]
void main()
{
	light = min(mix(max(data.y, data.z * sunLightIntensity), 1.0, brightness), 1.0);
	shade = toggleAO == 1 ? data.x : 1.0;
	texCoords = texCoord;
	gl_Position = projTrans * position;
}
