package com.travall.game.blocks.materials;

public class WaterMaterial extends Material {
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public boolean isTransparent() {
		return true;
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean hasCollision() {
		return false;
	}

	@Override
	public boolean canBlockSunRay() {
		return true;
	}

	@Override
	public boolean canBlockLights() {
		return false;
	}
}
