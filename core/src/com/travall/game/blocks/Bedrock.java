package com.travall.game.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.travall.game.tools.AtlasInfo;

public class Bedrock extends Block {
    public static boolean transparent = false;
    public static AtlasInfo atlasInfo = new AtlasInfo(0,0,48,64);

    public Bedrock(Texture ultimate) {
        super(ultimate, transparent, atlasInfo);
    }
}
