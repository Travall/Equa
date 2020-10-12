package com.travall.game.blocks;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.SlabModel;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class Slab extends Block {
	
	public Slab(int blockID) {
		super(blockID);
		this.model = new SlabModel(this, UltimateTexture.createRegion(7, 1));
		this.material = Material.SLAB;
		this.boundingBoxes.add(new BoundingBox(MIN.setZero(), MAX.set(1, 0.5f, 1)));
	}
	
	@Override
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		if (face == Facing.UP) return true;
		return super.canAddFace(primaray, secondary, face);
	}
	
	@Override
	public boolean isFaceSolid(BlockPos pos, Facing face) {
		return face == Facing.DOWN;
	}
}
