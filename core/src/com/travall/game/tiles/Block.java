package com.travall.game.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.tools.AtlasInfo;
import com.travall.game.tools.Utils;

public class Block {
    public boolean transparent;
    public TextureRegion texture;

    public Block(Texture ultimate, boolean transparent, AtlasInfo atlasInfo) {
        this.transparent = transparent;
        this.texture = new TextureRegion(ultimate,atlasInfo.x,atlasInfo.y,atlasInfo.width,atlasInfo.height);
    }

}
