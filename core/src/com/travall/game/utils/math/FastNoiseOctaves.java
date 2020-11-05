package com.travall.game.utils.math;

import java.util.Random;

public class FastNoiseOctaves {
	private final int[] octaves;
    private final float[] frequencies;
    private final float[] amplitudes;

    public FastNoiseOctaves(int octaveCount,double persistence, long seed) {
        octaves = new int[octaveCount];
        frequencies = new float[octaveCount];
        amplitudes = new float[octaveCount];

        Random rng = new Random(seed);
        for(int i = 0; i < octaveCount; i++) {
            octaves[i] = rng.nextInt();

            frequencies[i] = (float)Math.pow(2,i);
            amplitudes[i] = (float)Math.pow(persistence,octaveCount-i);
        }
    }

    public float getNoise(float x, float y) {
    	float result = 0;

        for(int i = 0; i < octaves.length; i++)  {
            result += FastNoise.getPerlin(octaves[i], x / frequencies[i], y / frequencies[i]) * amplitudes[i];
        }

        return result;
    }

    public float getNoise(float x, float y, float z) {
    	float result = 0;

        for(int i = 0; i < octaves.length; i++)  {
            result += FastNoise.getPerlin(octaves[i], x / frequencies[i], y / frequencies[i], z / frequencies[i]) * amplitudes[i];
        }

        return result;
    }
}
