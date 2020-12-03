package com.travall.game.blocks;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CactusModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.ui.actors.BlockSeletion;
import com.travall.game.utils.AmbiantType;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;
import com.travall.game.utils.UpdateState;

public class Cactus extends Block {

	public Cactus(int blockID) {
		super(blockID);
		this.model = new CactusModel(this, new BlockTextures(UltimateTexture.createRegion(11, 0), UltimateTexture.createRegion(11, 1), UltimateTexture.createRegion(11, 2)));
		this.boundingBoxes.add(new BoundingBox(MIN.set(0.125f,0,0.125f), MAX.set(0.875f, 1, 0.875f)));
		this.material = Material.CACTUS;
		BlockSeletion.add(this);
	}
	
	@Override
	public void onNeighbourUpdate(BlockPos primaray, BlockPos secondary, Facing face, UpdateState state) {
		if (accept(world.getBlock(primaray.offset(Facing.DOWN)))) return;
		
		onDestroy(primaray);
	}
	
	private boolean accept(Block block) {
		return block == BlocksList.SAND || block == BlocksList.CACTUS;
	}
	
	@Override
	public AmbiantType getAmbiantType() {
		return AmbiantType.DARKEN;
	}
}