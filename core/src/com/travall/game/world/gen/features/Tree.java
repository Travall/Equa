package com.travall.game.world.gen.features;

import com.travall.game.items.BlockItem;
import com.travall.game.utils.BlockPos;
import com.travall.game.blocks.BlocksList;
import com.travall.game.blocks.Log;
import com.travall.game.blocks.Leaves;

public final class Tree {
	private static final BlockPos pos = new BlockPos();
	
	public static void create(TreeType type, int x, int y, int z) {
		type.log.setBlock(pos.set(x, y+1, z));
		type.log.setBlock(pos.set(x, y+2, z));
		type.log.setBlock(pos.set(x, y+3, z));

		for (int xx = x - 2; xx <= x + 2; xx++) {
			for (int zz = z - 2; zz <= z + 2; zz++) {
				type.leaves.setBlock(pos.set(xx, y+4, zz));
			}
		}

		for (int xx = x - 1; xx <= x + 1; xx++) {
			for (int zz = z - 1; zz <= z + 1; zz++) {
				type.leaves.setBlock(pos.set(xx, y+5, zz));
			}
		}

		type.log.setBlock(pos.set(x, y+4, z));
		type.leaves.setBlock(pos.set(x, y+6, z));
	}
	
	public static enum TreeType {
		NORMAL(new BlockItem(BlocksList.LOG, Log.NORMAL_LOG), new BlockItem(BlocksList.LEAVES, Leaves.NORMAL_LEAVES)), 
		FLAX(new BlockItem(BlocksList.LOG, Log.FLAX_LOG), new BlockItem(BlocksList.LEAVES, Leaves.FLAX_LEAVES));
		
		public final BlockItem log, leaves;
		
		private TreeType(BlockItem log, BlockItem leaves) {
			this.log = log;
			this.leaves = leaves;
		}
	}
}
