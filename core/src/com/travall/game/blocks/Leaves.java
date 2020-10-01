package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Leaves extends Block {
	public static short id = 9;

	public Leaves(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(2, 2));
		this.material = Material.LEAVES;
	}
}