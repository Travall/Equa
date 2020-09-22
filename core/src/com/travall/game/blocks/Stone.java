package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Stone extends Block {
    public static boolean transparent = false;

    public Stone(UltimateTexture ultimate) {
        super(transparent, new BlockTextures(ultimate.createRegion(1, 1)));
    }
}