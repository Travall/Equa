package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Cactus extends Block {

	public Cactus(int blockID) {
		super(blockID);
		this.model = new CubeModel(this, new BlockTextures(UltimateTexture.createRegion(11, 0), UltimateTexture.createRegion(11, 1), UltimateTexture.createRegion(11, 2)));
		this.material = Material.BLOCK;
	}
}