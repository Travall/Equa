package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Log extends Block {
    public static short id = 8;
    public static boolean transparent = false;

    public Log(UltimateTexture ultimate) {
        super(transparent, new BlockTextures(ultimate.createRegion(0, 2),ultimate.createRegion(1, 2)));
    }
}