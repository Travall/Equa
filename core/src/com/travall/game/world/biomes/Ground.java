package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;

public class Ground extends Biome {
	public Ground() {
		this.name = "Ground";
		this.heightOctaves = 8;
		this.heightPersistence = 0.4;
		this.heightModifier = 1.0;
		this.size = 0.05;
		this.top = BlocksList.GRASS;
		this.middle = BlocksList.DIRT;
		this.underwater = BlocksList.DIRT;
	}
}
