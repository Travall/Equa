package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;
import com.travall.game.tools.UltimateTexture;

public class Bedrock extends Block {
    public static short id = 1;
    public static boolean transparent = false;

    public Bedrock(UltimateTexture ultimate) {
        super(transparent, new BlockTextures(ultimate.createRegion(0, 0)));
    }
}
