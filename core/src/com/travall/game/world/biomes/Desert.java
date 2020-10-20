package com.travall.game.world.biomes;

import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.utils.math.OpenSimplexOctaves;

public class Desert extends Biome {
	public Desert() {
		this.heightOctaves = 8;
		this.heightPersistence = 0.2f;
		this.heightModifier = 1;
		this.top = BlocksList.SAND;
		this.middle = BlocksList.SAND;
		this.underwater = BlocksList.SAND;
	}
}
