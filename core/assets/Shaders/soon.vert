#version 100 
#ifdef GL_ES
precision highp float;
#endif

attribute vec4 position;
attribute vec2 texCoord;

varying vec2 texCoords;

uniform mat4 projTrans;

void main()
{
	texCoords = texCoord;
	gl_Position = projTrans * position;
}
