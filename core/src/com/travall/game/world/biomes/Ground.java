package com.travall.game.world.biomes;

import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.utils.math.OpenSimplexOctaves;

public class Ground extends Biome {
	public Ground() {
		this.heightOctaves = 8;
		this.heightPersistence = 0.4f;
		this.heightModifier = 1;
		this.top = BlocksList.SAND;
		this.middle = BlocksList.DIRT;
	}
}
