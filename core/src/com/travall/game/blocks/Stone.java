package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.ui.actors.BlockSeletion;

public class Stone extends Block {

	public Stone(int blockID) {
		super(blockID);
		this.model = new CubeModel(this, new BlockTextures(UltimateTexture.createRegion(1, 0)));
		this.material = Material.BLOCK;
		BlockSeletion.add(this);
	}
}