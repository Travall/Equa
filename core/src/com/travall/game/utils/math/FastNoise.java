package com.travall.game.utils.math;

import static com.badlogic.gdx.math.Interpolation.smoother;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public final class FastNoise {
	
	private static final Vector2[] GRAD_2D = {
			new Vector2(-1, -1), new Vector2(1, -1), new Vector2(-1, 1), new Vector2(1, 1),
			new Vector2(0, -1),  new Vector2(-1, 0), new Vector2(0, 1),  new Vector2(1, 0),
	};
	
	private static final Vector3[] GRAD_3D = {
			new Vector3(1, 1, 0), new Vector3(-1, 1, 0), new Vector3(1, -1, 0), new Vector3(-1, -1, 0),
			new Vector3(1, 0, 1), new Vector3(-1, 0, 1), new Vector3(1, 0, -1), new Vector3(-1, 0, -1),
			new Vector3(0, 1, 1), new Vector3(0, -1, 1), new Vector3(0, 1, -1), new Vector3(0, -1, -1),
			new Vector3(1, 1, 0), new Vector3(0, -1, 1), new Vector3(-1, 1, 0), new Vector3(0, -1, -1),
	};
	
	private final static int X_PRIME = 1619;
	private final static int Y_PRIME = 31337;
	private final static int Z_PRIME = 6971;
	
	private static float GradCoord2D(final int seed, int x, int y, float xd, float yd) {
		int hash = seed;
		hash ^= X_PRIME * x;
		hash ^= Y_PRIME * y;

		hash = hash * hash * hash * 60493;
		hash = (hash >> 13) ^ hash;

		final Vector2 g = GRAD_2D[hash & 7];

		return xd * g.x + yd * g.y;
	}
	
	private static float GradCoord3D(final int seed, int x, int y, int z, float xd, float yd, float zd) {
		int hash = seed;
		hash ^= X_PRIME * x;
		hash ^= Y_PRIME * y;
		hash ^= Z_PRIME * z;

		hash = hash * hash * hash * 60493;
		hash = (hash >> 13) ^ hash;

		final Vector3 g = GRAD_3D[hash & 15];

		return xd * g.x + yd * g.y + zd * g.z;
	}
	
	public static float getPerlin(final int seed, float x, float y) 
	{
		final int x0 = MathUtils.floor(x);
		final int y0 = MathUtils.floor(y);
		final int x1 = x0 + 1;
		final int y1 = y0 + 1;

		final float xd0 = x - x0;
		final float yd0 = y - y0;
		
		final float xd1 = xd0 - 1f;
		final float yd1 = yd0 - 1f;
		
		final float xs, ys;
		xs = smoother.apply(xd0);
		ys = smoother.apply(yd0);

		final float xf0 = MathUtils.lerp(GradCoord2D(seed, x0, y0, xd0, yd0), GradCoord2D(seed, x1, y0, xd1, yd0), xs);
		final float xf1 = MathUtils.lerp(GradCoord2D(seed, x0, y1, xd0, yd1), GradCoord2D(seed, x1, y1, xd1, yd1), xs);

		return MathUtils.lerp(xf0, xf1, ys);
	}
	
	public static float getPerlin(final int seed, float x, float y, float z) {
		final int x0 = MathUtils.floor(x);
		final int y0 = MathUtils.floor(y);
		final int z0 = MathUtils.floor(z);
		final int x1 = x0 + 1;
		final int y1 = y0 + 1;
		final int z1 = z0 + 1;
		
		final float xd0 = x - x0;
		final float yd0 = y - y0;
		final float zd0 = z - z0;

		final float xs, ys, zs;
		xs = smoother.apply(xd0);
		ys = smoother.apply(yd0);
		zs = smoother.apply(zd0);
		
		final float xd1 = xd0 - 1f;
		final float yd1 = yd0 - 1f;
		final float zd1 = zd0 - 1f;

		final float xf00 = MathUtils.lerp(GradCoord3D(seed, x0, y0, z0, xd0, yd0, zd0), GradCoord3D(seed, x1, y0, z0, xd1, yd0, zd0), xs);
		final float xf10 = MathUtils.lerp(GradCoord3D(seed, x0, y1, z0, xd0, yd1, zd0), GradCoord3D(seed, x1, y1, z0, xd1, yd1, zd0), xs);
		final float xf01 = MathUtils.lerp(GradCoord3D(seed, x0, y0, z1, xd0, yd0, zd1), GradCoord3D(seed, x1, y0, z1, xd1, yd0, zd1), xs);
		final float xf11 = MathUtils.lerp(GradCoord3D(seed, x0, y1, z1, xd0, yd1, zd1), GradCoord3D(seed, x1, y1, z1, xd1, yd1, zd1), xs);

		final float yf0 = MathUtils.lerp(xf00, xf10, ys);
		final float yf1 = MathUtils.lerp(xf01, xf11, ys);

		return MathUtils.lerp(yf0, yf1, zs);
	}
}
