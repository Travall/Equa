package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Sand extends Block {
	public static short id = 5;

	public Sand(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(0, 1));
		this.material = Material.BLOCK;
	}
}
