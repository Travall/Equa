package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Water extends Block {
	public static short id = 6;

	public Water(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(2, 1));
		this.material = Material.WATER;
	}
}