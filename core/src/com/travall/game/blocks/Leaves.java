package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Leaves extends Block {
    public static short id = 9;
    public static boolean transparent = false;
    public static boolean translucent = true;

    public Leaves(UltimateTexture ultimate) {
        super(transparent, translucent, new BlockTextures(ultimate.createRegion(2, 2)));
    }
}