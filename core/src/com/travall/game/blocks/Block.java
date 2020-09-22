package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;

public class Block {
    public boolean transparent;
    public BlockTextures textures;

    public Block(boolean transparent, BlockTextures textures) {
        this.transparent = transparent;
        this.textures = textures;
    }
}
