package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Dirt extends Block {
	public static short id = 3;

	public Dirt(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(2, 0));
		this.material = Material.BLOCK;
	}
}