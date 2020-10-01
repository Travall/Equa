package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Dirt extends Block {
	public static short id = 3;

	public Dirt(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(1, 0));
		this.material = Material.BLOCK;
	}
}