package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class Leaves extends Block {
	public static short id = 9;

	public Leaves(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(12, 0));
		this.material = Material.LEAVES;
	}
	
	@Override
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		return true;
	}
}