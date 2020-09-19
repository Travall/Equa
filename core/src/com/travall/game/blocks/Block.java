package com.travall.game.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.tools.AtlasInfo;

public class Block {
    public boolean transparent;
    public TextureRegion texture;

    public Block(Texture ultimate, boolean transparent, AtlasInfo atlasInfo) {
        this.transparent = transparent;
        this.texture = new TextureRegion(ultimate,atlasInfo.x,atlasInfo.y,atlasInfo.width,atlasInfo.height);
    }

}
