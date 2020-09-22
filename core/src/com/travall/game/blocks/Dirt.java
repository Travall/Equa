package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Dirt extends Block {
    public static boolean transparent = false;

    public Dirt(UltimateTexture ultimate) {
        super(transparent, new BlockTextures(ultimate.createRegion(1, 0)));
    }
}