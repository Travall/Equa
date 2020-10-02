package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Cactus extends Block {
	public static short id = 10;

	public Cactus(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(11, 0), ultimate.createRegion(11, 1), ultimate.createRegion(11, 2));
		this.material = Material.BLOCK;
	}
}