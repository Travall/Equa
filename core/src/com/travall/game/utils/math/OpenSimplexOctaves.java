package com.travall.game.utils.math;

import java.util.Random;

public class OpenSimplexOctaves {
	private final OpenSimplex[] octaves;
	private final double[] frequencies;
	private final double[] amplitudes;

	public OpenSimplexOctaves(int octaveCount, double persistence, long seed) {
		octaves = new OpenSimplex[octaveCount];
		frequencies = new double[octaveCount];
		amplitudes = new double[octaveCount];

		Random rng = new Random(seed);
		for (int i = 0; i < octaveCount; i++) {
			octaves[i] = new OpenSimplex(rng.nextLong());

			frequencies[i] = Math.pow(2, i);
			amplitudes[i] = Math.pow(persistence, octaveCount - i);
		}
	}

	public double getNoise(double x, double y) {
		double result = 0;

		for (int i = 0; i < octaves.length; i++) {
			result += octaves[i].eval(x / frequencies[i], y / frequencies[i]) * amplitudes[i];
		}

		return result;
	}

	public double getNoise(double x, double y, double z) {
		double result = 0;

		for (int i = 0; i < octaves.length; i++) {
			result += octaves[i].eval(x / frequencies[i], y / frequencies[i], z / frequencies[i]) * amplitudes[i];
		}

		return result;
	}
}
