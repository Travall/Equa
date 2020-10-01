package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Cactus extends Block {
    public static short id = 10;

    public Cactus(UltimateTexture ultimate) {
    	this.textures = new BlockTextures(ultimate.createRegion(3, 2),ultimate.createRegion(0, 3));
        this.material = Material.BLOCK;
    }
    
    @Override
    public String getName() {
    	return "Cactus";
    }
}