package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class Air extends Block {
	
	public Air(int blockID) {
		super(blockID);
		this.material = Material.AIR;
	}
	
	@Override
	public boolean isAir() {
		return true;
	}
	
	@Override
	public boolean isFaceSolid(BlockPos pos, Facing face) {
		return false;
	}
}
