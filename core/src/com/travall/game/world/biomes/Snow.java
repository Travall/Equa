package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;

public class Snow extends Biome {
	public Snow() {
		this.name = "Snow";
		this.heightOctaves = 8;
		this.heightPersistence = 0.4f;
		this.heightModifier = 1.5f;
		this.top = BlocksList.SNOW;
		this.middle = BlocksList.DIRT;
		this.underwater = BlocksList.DIRT;
	}
}
