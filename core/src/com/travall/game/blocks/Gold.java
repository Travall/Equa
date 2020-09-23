package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Gold extends Block {

	public Gold(UltimateTexture ultimate) {
		super(15, false, new BlockTextures(ultimate.createRegion(3, 1)));
	}
}
