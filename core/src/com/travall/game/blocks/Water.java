package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.blocks.models.SlabModel;
import com.travall.game.blocks.models.WaterModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Water extends Block {

	public Water(int blockID) {
		super(blockID);
		this.model = new WaterModel(this, UltimateTexture.createRegion(2, 1));
		this.material = Material.WATER;
	}
}