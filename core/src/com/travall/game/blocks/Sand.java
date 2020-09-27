package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Sand extends Block {
    public static short id = 5;
    public static boolean transparent = false;

    public Sand(UltimateTexture ultimate) {
        super(transparent, new BlockTextures(ultimate.createRegion(0, 1)));
    }
}

