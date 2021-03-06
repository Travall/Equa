package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;
import com.travall.game.items.BlockItem;

public class Ground extends Biome {
	public Ground() {
		this.name = "Ground";
		this.heightOctaves = 8;
		this.heightPersistence = 0.4;
		this.heightModifier = 1.0;
		this.size = 0.05;
		this.top = new BlockItem(BlocksList.GRASS);
		this.middle = new BlockItem(BlocksList.DIRT);
		this.underwater = new BlockItem(BlocksList.DIRT);
	}
}
