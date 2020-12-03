package com.travall.game.blocks;

import static com.travall.game.renderer.block.UltimateTexture.createRegion;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.ui.actors.BlockSeletion;
import com.travall.game.utils.AmbiantType;
import com.travall.game.utils.ArrayDir;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class Leaves extends Block {

	public static final int 
	NORMAL_LEAVES = 0,
	FLAX_LEAVES = 1;

	public Leaves(int blockID) {
		super(blockID);
		
		newTypeComponent(2);
		final ArrayDir<BlockTextures> textures = new ArrayDir<>(BlockTextures.class, 2);
		textures.put(NORMAL_LEAVES, new BlockTextures(createRegion(12, 0)));
		textures.put(FLAX_LEAVES, new BlockTextures(createRegion(14, 0)));
		this.model = new CubeModel(this, textures);
		this.material = Material.LEAVES;
		
		BlockSeletion.add(this, NORMAL_LEAVES);
		BlockSeletion.add(this, FLAX_LEAVES);
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