#version 310 es
#ifdef GL_ES
    precision lowp float;
#endif

out vec4 color;

in float yPos;

uniform float cloudPower;
uniform float cloudOffset;
uniform float cloudClamp;

void main() {
	float shade = (yPos + cloudOffset) * cloudPower;
	float a = 1.0 - clamp(shade, 0.0, cloudClamp);
	color = vec4(a, a, a, 0.04);
}
