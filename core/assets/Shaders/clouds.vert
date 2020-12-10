#version 310 es
#ifdef GL_ES
    precision highp float;
#endif

in vec3 a_position;

in vec2 offset;

out float yPos;

uniform float shift;
uniform float size;
uniform float rows;

uniform sampler2D noiseMap;
uniform mat4 projTrans;

void main() {
	float noise = texture(noiseMap, offset / rows).r;
	
	noise += shift;
	
	if (noise < 0.001) {
		noise *= size;
		yPos = a_position.y;
		
		vec3 newPos = (a_position * noise) + vec3(offset.x, 200.0, offset.y);
		gl_Position = projTrans * vec4(newPos, 1.0);
	} else {
		gl_Position = vec4(0.0);
	}
}