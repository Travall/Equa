package com.travall.game.blocks;

import static com.travall.game.renderer.block.UltimateTexture.createRegion;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.ui.actors.BlockSeletion;
import com.travall.game.utils.ArrayDir;

public class Log extends Block {
	
	public static final int
	NORMAL_LOG = 0,
	FLAX_LOG = 1;
	
	public Log(int blockID) {
		super(blockID);
		
		newTypeComponent(2);
		final ArrayDir<BlockTextures> textures = new ArrayDir<>(BlockTextures.class, 2);
		textures.put(0, new BlockTextures(createRegion(12, 2), createRegion(12, 1)));
		textures.put(1, new BlockTextures(createRegion(15, 2), createRegion(15, 1)));
		this.model = new CubeModel(this, textures);
		this.material = Material.BLOCK;
		
		BlockSeletion.add(this, 0);
		BlockSeletion.add(this, 1);
	}
}