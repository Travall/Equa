package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;

public class Snow extends Biome {
	public Snow() {
		this.heightOctaves = 8;
		this.heightPersistence = 0.4f;
		this.heightModifier = 1;
		this.top = BlocksList.LOG;
		this.middle = BlocksList.DIRT;
	}
}
