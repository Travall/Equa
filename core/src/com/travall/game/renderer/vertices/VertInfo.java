package com.travall.game.renderer.vertices;

import static com.travall.game.utils.AmbiantType.*;

import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
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

	private static final float[] AMB = {0.6f, 0.7f, 0.8f, 1f};

	private void vertAO(Block center, Block side1, Block side2, Block corner) {
		final boolean bool = side1.getAmbiantType() == FULLBRIGHT || side2.getAmbiantType() == FULLBRIGHT || center.getAmbiantType() == FULLBRIGHT;
		twoSides = side1.getAmbiantType() == DARKEN && side2.getAmbiantType() == DARKEN;
		
		if (bool) {
			ambLit *= AMB[3];
			return;
		}
		
		if (twoSides) {
			ambLit *= AMB[0];
			return;
		}
		
		if (bool || corner.getAmbiantType() == FULLBRIGHT) {
			ambLit *= AMB[3];
			return;
		}
		
		if (center.getAmbiantType() == DARKEN && corner.getAmbiantType() == DARKEN) {
			ambLit *= AMB[0];
			return;
		}
		
		ambLit *= 
		AMB[side1.getAmbiantType().value + side2.getAmbiantType().value + corner.getAmbiantType().value + center.getAmbiantType().value - 1];
	}

	public void calcLight(Block block, int center, int side1, int side2, int corner) {
		twoSides = false;
		if (!block.isSrclight()) vertAO(BlocksList.get(center), BlocksList.get(side1), BlocksList.get(side2), BlocksList.get(corner));
		
		int light;
		int lightTotal, lightCount = 1;

		int centerLight = BlockUtils.toSrcLight(center);
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
		
		lightCount = 1;
		centerLight = BlockUtils.toSunLight(center);
		lightTotal = centerLight;

		light = BlockUtils.toSunLight(side1);
		if (light != 0) {
			lightCount++;
			lightTotal += light;
		}

		light = BlockUtils.toSunLight(side2);
		if (light != 0) {
			lightCount++;
			lightTotal += light;
		}

		light = BlockUtils.toSunLight(corner);
		if (!twoSides && (light != 0 || centerLight == 1)) {
			lightCount++;
			lightTotal += (light == 1 && centerLight == 1) ? 0 : light;
		}

		sunLit = lightCount == 1 ? lightTotal / BlockUtils.lightScl : (lightTotal / lightCount) / BlockUtils.lightScl;
	}

	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void mul(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
	}

	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
}
