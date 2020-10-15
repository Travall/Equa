package com.travall.game.blocks;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CactusModel;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

import static com.travall.game.world.World.world;

public class Cactus extends Block {

	public Cactus(int blockID) {
		super(blockID);
		this.model = new CactusModel(this, new BlockTextures(UltimateTexture.createRegion(11, 0), UltimateTexture.createRegion(11, 1), UltimateTexture.createRegion(11, 2)));
		this.boundingBoxes.add(new BoundingBox(MIN.set(0.125f,0,0.125f), MAX.set(0.875f, 1, 0.875f)));
		this.material = Material.CACTUS;
	}

	@Override
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		if (face == Facing.NORTH || face == Facing.SOUTH || face == Facing.EAST || face == Facing.WEST) {
			return true;
		}
		return super.canAddFace(primaray, secondary, face);
	}

}