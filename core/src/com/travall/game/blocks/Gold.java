package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Gold extends Block {
	public static short id = 7;

	public Gold(UltimateTexture ultimate) {
		this.textures = new BlockTextures(ultimate.createRegion(3, 1));
		this.material = Material.BLOCK;
		this.lightLevel = 15;
	}
	
	@Override
    public String getName() {
    	return "Gold";
    }
}
