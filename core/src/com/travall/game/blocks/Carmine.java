package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Carmine extends Block {

	public Carmine(int blockID) {
		super(blockID);
		this.model = new CubeModel(this, new BlockTextures(UltimateTexture.createRegion(4, 1), UltimateTexture.createRegion(3, 1), UltimateTexture.createRegion(2, 0)));
		this.material = Material.BLOCK;
	}
}
