package com.travall.game.blocks;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.SlabModel;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;
import com.travall.game.utils.Facing.Axis;

public class Slab extends Block {
	
	public Slab(int blockID) {
		super(blockID);
		this.model = new SlabModel(this, UltimateTexture.createRegion(7, 1));
		this.material = Material.SLAB;
		this.boundingBoxes.add(new BoundingBox(MIN.setZero(), MAX.set(1, 0.5f, 1)));
	}
	
	@Override
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		boolean bool = super.canAddFace(primaray, secondary, face);
		if (!bool) {
			bool = face == Facing.UP;
			if (!bool) bool = face.axis == Axis.Y && world.getBlock(secondary) instanceof Slab;
		}
		return bool;
	}
}
