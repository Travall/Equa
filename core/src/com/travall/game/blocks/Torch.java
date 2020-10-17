package com.travall.game.blocks;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.SlabModel;
import com.travall.game.blocks.models.TorchModel;
import com.travall.game.entities.Player;
import com.travall.game.handles.Raycast;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

import static com.travall.game.world.World.world;

public class Torch extends Block {

	public Torch(int blockID) {
		super(blockID);
		this.model = new TorchModel(this, new BlockTextures(UltimateTexture.createRegion(14, 2),UltimateTexture.createRegion(14, 1)));
		this.material = Material.TORCH;
		this.boundingBoxes.add(new BoundingBox(MIN.set(7/16f,0,7/16f), MAX.set(9/16f, 10/16f, 9/16f)));
		this.lightLevel = 15;
	}

	@Override
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		if (face == Facing.NORTH || face == Facing.SOUTH || face == Facing.EAST || face == Facing.WEST || face == Facing.UP) {
			return true;
		}
		return super.canAddFace(primaray, secondary, face);
	}

}
