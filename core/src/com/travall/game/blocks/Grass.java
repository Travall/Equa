package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Grass extends Block {

	public Grass(int blockID) {
		super(blockID);
		this.model = new CubeModel(this, new BlockTextures(UltimateTexture.createRegion(4, 0), UltimateTexture.createRegion(3, 0), UltimateTexture.createRegion(2, 0)));
		this.material = Material.BLOCK;
	}
}
