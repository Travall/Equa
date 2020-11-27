package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;

public class Desert extends Biome {
	public Desert() {
		this.name = "Desert";
		this.heightOctaves = 7;
		this.heightPersistence = 0.3;
		this.heightModifier = 0.6;
		this.size = -0.1;
		this.top = BlocksList.SAND;
		this.middle = BlocksList.SAND;
		this.underwater = BlocksList.SAND;
	}
}
