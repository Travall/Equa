package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Grass extends Block {
    public static short id = 4;
    public static boolean transparent = false;

    public Grass(UltimateTexture ultimate) {
    	super(transparent, new BlockTextures(ultimate.createRegion(3, 0), ultimate.createRegion(2, 0), ultimate.createRegion(1, 0)));
    }
}

