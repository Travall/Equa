package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;

public class Carmine extends Biome {
	public Carmine() {
		this.name = "Carmine";
		this.heightOctaves = 8;
		this.heightPersistence = 0.4;
		this.heightModifier = 1.0;
		this.size = -0.08;
		this.top = BlocksList.CARMINE;
		this.middle = BlocksList.DIRT;
		this.underwater = BlocksList.DIRT;
	}
}
