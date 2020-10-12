package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;

public class Air extends Block {
	
	public Air(int blockID) {
		super(blockID);
		this.material = Material.AIR;
	}
	
	@Override
	public boolean isAir() {
		return true;
	}
}
