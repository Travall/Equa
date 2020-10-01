package com.travall.game.blocks;

import com.travall.game.blocks.materials.Material;

public class Air extends Block {
	public static short id = 0;
	public static boolean transparent = true;

	public Air() {
		this.material = Material.AIR;
	}
}
