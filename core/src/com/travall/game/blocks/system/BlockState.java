package com.travall.game.blocks.system;

import static com.travall.game.world.World.world;

import com.travall.game.blocks.Block;
import com.travall.game.utils.BlockPos;
import com.travall.game.world.World;

public class BlockState {
	static final boolean DEBUG = false;
	
	private final Block block;
	
	private BlockState(Block block) {
		this.block = block;
	}
	
	public boolean isSolidFace(BlockPos primary, BlockPos secondry) {
		return block.getMaterial().isTransparent();
	}
	
	public Block getBlock() {
		return block;
	}
}
