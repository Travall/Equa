package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Bedrock extends Block {
	public static short id = 1;

	public Bedrock(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(0, 0));
		this.material = Material.BLOCK;
	}
}
