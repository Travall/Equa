package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Log extends Block {
	public static short id = 8;

	public Log(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(12, 2), ultimate.createRegion(12, 1));
		this.material = Material.BLOCK;
	}
}