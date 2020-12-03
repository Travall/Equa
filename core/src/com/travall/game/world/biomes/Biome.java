package com.travall.game.world.biomes;

import com.travall.game.items.BlockItem;
import com.travall.game.utils.math.OpenSimplexOctaves;

public class Biome {
	public String name;
	public int heightOctaves;
	public double heightPersistence;
	public int decisionOctaves = 7;
	public double decisionPersistence = 0.4;
	public double heightModifier;
	public double size = 0.0;
	public BlockItem top;
	public BlockItem middle;
	public BlockItem underwater;
	public OpenSimplexOctaves heightMap;
	public OpenSimplexOctaves decisionMap;
	
	@Override
	public String toString() {
		return name;
	}
}
