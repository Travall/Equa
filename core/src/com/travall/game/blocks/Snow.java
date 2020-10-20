package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Snow extends Block {

	public Snow(int blockID) {
		super(blockID);
		this.model = new CubeModel(this, new BlockTextures(UltimateTexture.createRegion(6, 0), UltimateTexture.createRegion(5, 0), UltimateTexture.createRegion(2, 0)));
		this.material = Material.BLOCK;
	}
}
