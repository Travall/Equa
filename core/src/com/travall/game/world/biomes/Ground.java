package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;

public class Ground extends Biome {
	public Ground() {
		this.heightOctaves = 8;
		this.heightPersistence = 0.4f;
		this.heightModifier = 1f;
		this.top = BlocksList.GRASS;
		this.middle = BlocksList.DIRT;
		this.underwater = BlocksList.DIRT;
	}
}
