package com.travall.game.utils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.travall.game.utils.math.Size;

public class Utils {
	
	/** A cached screen size. */
	public static final Size screen = new Size();
	/** A cached world screen size. */
	public static final Size world  = new Size();

	public static double normalize(double Input, int max) {
		return ((Input - -1) / (1 - -1) * (max - 0) + 0);
	}

	public static int[] locateAttributes(ShaderProgram shader, VertexAttributes attributes) {
		final int s = attributes.size();
		final int[] locations = new int[s];
		for (int i = 0; i < s; i++) {
			final VertexAttribute attribute = attributes.get(i);
			locations[i] = shader.getAttributeLocation(attribute.alias);
		}
		return locations;
	}

	public static float gamma(double num) {
		return (float)Math.pow(num, 2.2);
	}

	public static int createANDbits(final int bitSize) {
		return -1 >>> 32 - bitSize;
	}

	public static boolean inBounds(int index, int length) {
		return (index >= 0) && (index < length);
	}

}
