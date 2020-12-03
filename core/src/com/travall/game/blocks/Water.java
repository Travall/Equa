package com.travall.game.blocks;

import static com.travall.game.world.World.world;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.WaterModel;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.ui.actors.BlockSeletion;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class Water extends Block {

	public Water(int blockID) {
		super(blockID);
		this.model = new WaterModel(this, UltimateTexture.createRegion(2, 1));
		this.material = Material.WATER;
		BlockSeletion.add(this);
	}
	
	@Override
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		if (face == Facing.UP && world.getBlock(secondary) != BlocksList.WATER) {
			return true;
		}
		return super.canAddFace(primaray, secondary, face);
	}
}