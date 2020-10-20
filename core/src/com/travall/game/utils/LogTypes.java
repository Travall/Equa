package com.travall.game.utils;

import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public enum LogTypes {
	/** Facing Y+ */
	OAK  (new BlockTextures(UltimateTexture.createRegion(12, 2), UltimateTexture.createRegion(12, 1))),
	/** Facing Y- */
	DARKOAK (new BlockTextures(UltimateTexture.createRegion(15, 2), UltimateTexture.createRegion(15, 1)));

	public BlockTextures textures;

	private LogTypes(BlockTextures textures) {
		this.textures = textures;
	}
}
