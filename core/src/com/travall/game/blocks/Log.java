package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Log extends Block {
    public static short id = 8;

    public Log(UltimateTexture ultimate) {
    	this.textures = new BlockTextures(ultimate.createRegion(0, 2),ultimate.createRegion(1, 2));
        this.material = Material.BLOCK;
    }
    
    @Override
    public String getName() {
    	return "Log";
    }
}