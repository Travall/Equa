package com.travall.game.utils.math;

import com.badlogic.gdx.math.RandomXS128;

public class OpenSimplexOctaves {
    private OpenSimplex[] octaves;
    private double[] frequencies;
    private double[] amplitudes;

    private int octaveCount;
    private double persistence;
    private long seed;

    public OpenSimplexOctaves(int octaveCount,double persistence, long seed) {
        this.octaveCount = octaveCount;
        this.persistence = persistence;
        this.seed = seed;

        octaves = new OpenSimplex[octaveCount];
        frequencies = new double[octaveCount];
        amplitudes = new double[octaveCount];

        RandomXS128 rng = new RandomXS128(seed);
        for(int i = 0; i < octaveCount; i++) {
            octaves[i] = new OpenSimplex(rng.nextLong());

            frequencies[i] = Math.pow(2,i);
            amplitudes[i] = Math.pow(persistence,octaveCount-i);
        }
    }

    public double getNoise(int x, int y) {
        double result = 0;

        for(int i = 0; i < octaves.length; i++)  {
            result += octaves[i].eval(x / frequencies[i], y / frequencies[i]) * amplitudes[i];
        }

        return result;
    }

    public double getNoise(int x, int y, int z) {
        double result = 0;

        for(int i = 0; i < octaves.length; i++)  {
            result += octaves[i].eval(x / frequencies[i], y / frequencies[i], z / frequencies[i]) * amplitudes[i];
        }

        return result;
    }
}

