package com.travall.game.world.biomes;

import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.utils.math.OpenSimplexOctaves;

public class Biome {
	public int heightOctaves;
	public float heightPersistence;
	public int decisionOctaves = 8;
	public float decisionPersistence = 0.4f;
	public float heightModifier;
	public Block top;
	public Block middle;
	public OpenSimplexOctaves heightMap;
	public OpenSimplexOctaves decisionMap;
}