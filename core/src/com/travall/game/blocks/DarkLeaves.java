package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.utils.AmbiantType;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class DarkLeaves extends Block {

	public DarkLeaves(int blockID) {
		super(blockID);
		this.model = new CubeModel(this, new BlockTextures(UltimateTexture.createRegion(14, 0)));
		this.material = Material.LEAVES;
	}
	
	@Override
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		return true;
	}
	
	@Override
	public AmbiantType getAmbiantType() {
		return AmbiantType.DARKEN;
	}
}