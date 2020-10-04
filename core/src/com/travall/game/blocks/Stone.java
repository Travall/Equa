package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Stone extends Block {
	public static short id = 2;

	public Stone(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(1, 0));
		this.material = Material.BLOCK;
	}
}