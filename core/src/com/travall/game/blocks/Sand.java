package com.travall.game.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.travall.game.tools.AtlasInfo;

public class Sand extends Block {
    public static boolean transparent = false;
    public static AtlasInfo atlasInfo = new AtlasInfo(144,0,48,64);

    public Sand(Texture ultimate) {
        super(ultimate, transparent, atlasInfo);
    }
}

