package com.travall.game.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.travall.game.tools.AtlasInfo;

public class Stone extends Block {
    public static boolean transparent = false;
    public static AtlasInfo atlasInfo = new AtlasInfo(192,0,48,64);

    public Stone(Texture ultimate) {
        super(ultimate, transparent, atlasInfo);
    }
}