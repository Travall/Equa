package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Water extends Block {
    public static boolean transparent = true;

    public Water(UltimateTexture ultimate) {
        super(transparent, new BlockTextures(ultimate.createRegion(2, 1)));
    }
}