package com.travall.game.blocks;

import com.travall.game.tools.BlockTextures;

public class Block {
    public boolean transparent;
    public boolean translucent;
    public BlockTextures textures;
    public int srclight;

    public Block(boolean transparent, BlockTextures textures) {
        this.transparent = transparent;
        this.textures = textures;
    }

    public Block(boolean transparent, boolean translucent, BlockTextures textures) {
        this.transparent = transparent;
        this.translucent = translucent;
        this.textures = textures;
    }
    
    public Block(int srclight, boolean transparent, BlockTextures textures) {
        this.transparent = transparent;
        this.textures = textures;
        this.srclight = srclight;
    }
    
    public boolean isSrclight() {
    	return srclight != 0;
    }
}
