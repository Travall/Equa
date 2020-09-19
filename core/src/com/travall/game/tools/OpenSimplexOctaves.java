package com.travall.game.tools;

import java.util.Random;

public class OpenSimplexOctaves {
    private OpenSimplex[] octaves;
    private double[] frequencies;
    private double[] amplitudes;

    private int octaveCount;
    private double persistence;
    private int seed;

    public OpenSimplexOctaves(int octaveCount,double persistence,int seed) {
        this.octaveCount = octaveCount;
        this.persistence = persistence;
        this.seed = seed;

        octaves = new OpenSimplex[octaveCount];
        frequencies = new double[octaveCount];
        amplitudes = new double[octaveCount];

        Random rng = new Random(seed);

        for(int i = 0; i < octaveCount; i++) {
            octaves[i] = new OpenSimplex(rng.nextInt());

            frequencies[i] = Math.pow(2,i);
            amplitudes[i] = Math.pow(persistence,octaves.length-i);
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

