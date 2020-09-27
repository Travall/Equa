package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Air extends Block {
    public static short id = 0;
    public static boolean transparent = true;

    public Air(UltimateTexture ultimate) {
        super(transparent, new BlockTextures(ultimate.createRegion(0, 0)));
    }
}
