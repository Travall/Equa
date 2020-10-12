package com.travall.game.blocks;

import com.badlogic.gdx.utils.IntMap;

public final class BlocksList {
	// Changed from HashMap to IntMap to avoid object baking from "short" key.
	private static final IntMap<Block> types = new IntMap<Block>();
	
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
	SLAB = new Slab(ID++);
	
	public static final int SIZE = ID;

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

		hasInts = true;
	}
	
	private static void addBlock(final Block block) {
		types.put(block.getID(), block);
	}
	
	public static short toBlockData(int ID, int data) {
		return (short)((data<<8)|ID);
	}
	
	public static short toBlockData(short ID, int data) {
		return (short)((data<<8)|obtainID(ID));
	}
	
	public static int obtainID(short block) {
		return block&0xFF;
	}
	
	public static int obtainData(short block) {
		return block>>>8;
	}
	
	public static Block get(final int ID) {
		return types.get(ID&0xFF);
	}
	
	public static boolean equals(int ID, Block block) {
		return (ID&0xFF) == block.getID();
	}
	
	public static boolean equals(short ID, Block block) {
		return obtainID(ID) == block.getID();
	}
}
