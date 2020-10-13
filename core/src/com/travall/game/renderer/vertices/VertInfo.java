package com.travall.game.renderer.vertices;

import static com.travall.game.world.World.world;

import com.travall.game.blocks.Block;
import com.travall.game.utils.AmbiantType;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.BlockUtils;

public class VertInfo {
	/** Positions */
	public float x, y, z;
	/** Lighting. Must clamp it to 0 to 1 if necessary. Use the <code>MathUtils.clamp(value, 0f, 1f)</code> */
	public float ambLit = 1f, srcLit, sunLit = 1f;

	/** Cache boolean of "is two sides" This boolean is for fix light leakage. */
	private boolean twoSides;

	public float packData() {
		return Float.intBitsToFloat((((int) (255*sunLit)<<16) | ((int) (255*srcLit)<<8) | ((int) (255*ambLit))));
	}

	private static final float[] AMB = {0.5f, 0.65f, 0.75f, 1f};;

	private void vertAO(AmbiantType side1, AmbiantType side2, AmbiantType corner) {
		final boolean bool = side1 == AmbiantType.FULLBRIGHT || side2 == AmbiantType.FULLBRIGHT;
		if (side1 == AmbiantType.DARKEN && side2 == AmbiantType.DARKEN) {
			twoSides = true;
			if (bool) return;
			ambLit *= AMB[0];
			return;
		}
		if (bool || corner == AmbiantType.FULLBRIGHT) return;
		ambLit *= AMB[side1.value + side2.value + corner.value];
	}

	private void smoothLight(int center, int side1, int side2, int corner) {
		int light;
		int lightTotal, lightCount = 1;

		final int centerLight = BlockUtils.toSrcLight(center);
		lightTotal = centerLight;

		light = BlockUtils.toSrcLight(side1);
		if (light != 0) {
			lightCount++;
			lightTotal += light;
		}

		light = BlockUtils.toSrcLight(side2);
		if (light != 0) {
			lightCount++;
			lightTotal += light;
		}

		light = BlockUtils.toSrcLight(corner);
		if (!twoSides && (light != 0 || centerLight == 1)) {
			lightCount++;
			lightTotal += (light == 1 && centerLight == 1) ? 0 : light;
		}

		srcLit = lightCount == 1 ? lightTotal / BlockUtils.lightScl : (lightTotal / lightCount) / BlockUtils.lightScl;
	}

	public void calcLight(Block block, BlockPos center, BlockPos side1, BlockPos side2, BlockPos corner) {
		twoSides = false;
		if (!block.isSrclight())
			vertAO(world.getBlock(side1).getAmbiantType(), world.getBlock(side2).getAmbiantType(), world.getBlock(corner).getAmbiantType());
		
		smoothLight(world.getData(center), world.getData(side1), world.getData(side2), world.getData(corner));
	}

	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
