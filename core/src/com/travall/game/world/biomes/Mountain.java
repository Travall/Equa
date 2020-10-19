package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;

public class Mountain extends Biome {
	public Mountain() {
		this.heightOctaves = 8;
		this.heightPersistence = 0.4f;
		this.heightModifier = 1;
		this.top = BlocksList.DIRT;
		this.middle = BlocksList.DIRT;
	}
}
