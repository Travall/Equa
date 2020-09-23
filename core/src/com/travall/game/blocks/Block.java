package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;

public class Block {
    public boolean transparent;
    public BlockTextures textures;
    public int srclight;

    public Block(boolean transparent, BlockTextures textures) {
        this.transparent = transparent;
        this.textures = textures;
    }
    
    public Block(int srclight, boolean transparent, BlockTextures textures) {
        this.transparent = transparent;
        this.textures = textures;
    }
    
    public boolean isSrclight() {
    	return srclight != 0;
    }
}
