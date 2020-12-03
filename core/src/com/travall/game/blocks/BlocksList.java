package com.travall.game.blocks;

import com.travall.game.utils.BlockUtils;

public final class BlocksList {	
	private static boolean hasInts;
	private static int ID = 0;
	
	public static final Block
	AIR = new Air(ID++),
	BEDROCK = new Bedrock(ID++),
	STONE = new Stone(ID++),
	DIRT = new Dirt(ID++),
	GRASS = new Grass(ID++),
	SAND = new Sand(ID++),
	WATER = new Water(ID++),
	GOLD = new Gold(ID++),
	LOG = new Log(ID++),
	LEAVES = new Leaves(ID++),
	CACTUS = new Cactus(ID++),
	SLAB = new Slab(ID++),
	TALLGRASS = new TallGrass(ID++),
	SHRUB = new Shrub(ID++),
	TORCH = new Torch(ID++),
	DOOR = new Door(ID++),
	DARKSHRUB = new FlaxShrub(ID++);


	public static final int SIZE = ID;
	private static final Block[] blocks = new Block[SIZE];

	public static void ints() {
		if (hasInts) return;
		
		addBlock(AIR);
		addBlock(BEDROCK);
		addBlock(STONE);
		addBlock(DIRT);
		addBlock(GRASS);
		addBlock(SAND);
		addBlock(WATER);
		addBlock(GOLD);
		addBlock(LOG);
		addBlock(LEAVES);
		addBlock(CACTUS);
		addBlock(SLAB);
		addBlock(TALLGRASS);
		addBlock(SHRUB);
		addBlock(TORCH);
		addBlock(DOOR);
		addBlock(DARKSHRUB);

		hasInts = true;
	}
	
	private static void addBlock(final Block block) {
		blocks[block.getID()] = block;
	}
	
	public static Block get(final int data) {
		return blocks[BlockUtils.toBlockID(data)];
	}
}
