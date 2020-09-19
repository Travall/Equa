package com.travall.game.tiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.tools.AtlasInfo;

public class Water extends Block {
    public static boolean transparent = true;
    public static AtlasInfo atlasInfo = new AtlasInfo(0,64,48,64);

    public Water(Texture ultimate) {
        super(ultimate, transparent, atlasInfo);
    }
}