package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;
import com.travall.game.blocks.Grass;
import com.travall.game.items.BlockItem;

public class Flaxen extends Biome {
	public Flaxen() {
		this.name = "Flaxen";
		this.heightOctaves = 8;
		this.heightPersistence = 0.4;
		this.heightModifier = 1.0;
		this.size = -0.08;
		this.top = new BlockItem(BlocksList.GRASS, Grass.FLAX_GRASS);
		this.middle = new BlockItem(BlocksList.DIRT);
		this.underwater = new BlockItem(BlocksList.DIRT);
	}
}
