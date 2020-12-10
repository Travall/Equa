#version 310 es
#ifdef GL_ES
    precision highp float;
#endif

in vec2 coord;

out float value;

uniform int octave;
uniform float gain; // 0.5
uniform float scale;
uniform float time;
uniform float move;

const vec3[16] grad = vec3[] (
	vec3(1, 1, 0), vec3(-1, 1, 0), vec3(1, -1, 0), vec3(-1, -1, 0),
	vec3(1, 0, 1), vec3(-1, 0, 1), vec3(1, 0, -1), vec3(-1, 0, -1),
	vec3(0, 1, 1), vec3(0, -1, 1), vec3(0, 1, -1), vec3(0, -1, -1),
	vec3(1, 1, 0), vec3(0, -1, 1), vec3(-1, 1, 0), vec3(0, -1, -1)
);

vec3 hermite(vec3 a) {
	return a * a * a * (a * (a * 6.0 - 15.0) + 10.0);
}

float gradcoord(int hash, int x, int y, int z, float xd, float yd, float zd) {
	hash ^= 1619  * x;
	hash ^= 31337 * y;
	hash ^= 6971  * z;

	hash = hash * hash * hash * 60493;
	hash = (hash >> 13) ^ hash;

	vec3 g = grad[hash & 15];

	return xd * g.x + yd * g.y + zd * g.z;
}

float noise(int seed, vec3 pos) {
	vec3 posf = floor(pos);
	ivec3 pos0 = ivec3(posf);
	ivec3 pos1 = pos0 + 1;

	vec3 posd0 = pos - posf;
	vec3 posd1 = posd0 - 1.0;
		
	vec3 poss = hermite(posd0);

	float xf00 = mix(gradcoord(seed, pos0.x, pos0.y, pos0.z, posd0.x, posd0.y, posd0.z), gradcoord(seed, pos1.x, pos0.y, pos0.z, posd1.x, posd0.y, posd0.z), poss.x);
	float xf10 = mix(gradcoord(seed, pos0.x, pos1.y, pos0.z, posd0.x, posd1.y, posd0.z), gradcoord(seed, pos1.x, pos1.y, pos0.z, posd1.x, posd1.y, posd0.z), poss.x);
	float xf01 = mix(gradcoord(seed, pos0.x, pos0.y, pos1.z, posd0.x, posd0.y, posd1.z), gradcoord(seed, pos1.x, pos0.y, pos1.z, posd1.x, posd0.y, posd1.z), poss.x);
	float xf11 = mix(gradcoord(seed, pos0.x, pos1.y, pos1.z, posd0.x, posd1.y, posd1.z), gradcoord(seed, pos1.x, pos1.y, pos1.z, posd1.x, posd1.y, posd1.z), poss.x);

	float yf0 = mix(xf00, xf10, poss.y);
	float yf1 = mix(xf01, xf11, poss.y);

	return mix(yf0, yf1, poss.z);
}

float fbm(int seed, vec3 x) {
	float v = 0.0;
	float a = 1.0;
	for (int i = 0; i < octave; ++i) {
		v += a * noise(++seed, x);
		x.xy *= 2.0;
		a *= gain;
	}
	return v;
}

void main() {
	float shift = time * move;
 	value = fbm(13378439, vec3((coord.x + shift) * scale, (coord.y + shift) * scale, time));
}