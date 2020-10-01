package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Bedrock extends Block {
    public static short id = 1;

    public Bedrock(UltimateTexture ultimate) {
        this.textures = new BlockTextures(ultimate.createRegion(0, 0));
        this.material = Material.BLOCK;
    }
    
    @Override
    public String getName() {
    	return "Bedrock";
    }
}
