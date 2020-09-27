package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Cactus extends Block {
    public static short id = 10;
    public static boolean transparent = false;

    public Cactus(UltimateTexture ultimate) {
        super(transparent, new BlockTextures(ultimate.createRegion(3, 2),ultimate.createRegion(0, 3)));
    }
}