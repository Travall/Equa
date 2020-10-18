package com.travall.game.blocks.materials;

public class TorchMaterial extends Material {
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean isTransparent() {
		return false;
	}
	
	@Override
	public boolean hasCollision() {
		return false;
	}

	@Override
	public boolean canBlockSunRay() {
		return false;
	}

	@Override
	public boolean canBlockLights() {
		return false;
	}
}
