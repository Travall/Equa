package com.travall.game.world.biomes;

import com.travall.game.blocks.BlocksList;
import com.travall.game.items.BlockItem;

public class Desert extends Biome {
	public Desert() {
		this.name = "Desert";
		this.heightOctaves = 7;
		this.heightPersistence = 0.3;
		this.heightModifier = 0.6;
		this.size = -0.1;
		
		final BlockItem sand = new BlockItem(BlocksList.SAND);
		this.top = sand;
		this.middle = sand;
		this.underwater = sand;
	}
}
