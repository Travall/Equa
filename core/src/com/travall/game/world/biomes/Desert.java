package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;

public class Desert extends Biome {
	public Desert() {
		this.name = "Desert";
		this.heightOctaves = 8;
		this.heightPersistence = 0.2;
		this.heightModifier = 1.0;
		this.size = -0.1;
		this.top = BlocksList.SAND;
		this.middle = BlocksList.SAND;
		this.underwater = BlocksList.SAND;
	}
}
