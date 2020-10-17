package com.travall.game.blocks;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CactusModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Cactus extends Block {

	public Cactus(int blockID) {
		super(blockID);
		this.model = new CactusModel(this, new BlockTextures(UltimateTexture.createRegion(11, 0), UltimateTexture.createRegion(11, 1), UltimateTexture.createRegion(11, 2)));
		this.boundingBoxes.add(new BoundingBox(MIN.set(0.125f,0,0.125f), MAX.set(0.875f, 1, 0.875f)));
		this.material = Material.CACTUS;
	}
}