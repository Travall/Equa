package com.travall.game.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.travall.game.tools.AtlasInfo;

public class Water extends Block {
    public static boolean transparent = true;
    public static AtlasInfo atlasInfo = new AtlasInfo(0,64,48,64);

    public Water(Texture ultimate) {
        super(ultimate, transparent, atlasInfo);
    }
}