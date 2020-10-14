package com.travall.game.blocks;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.blocks.models.SlabModel;
import com.travall.game.blocks.models.WaterModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class WaterTop extends Block {

	public WaterTop(int blockID) {
		super(blockID);
		this.model = new WaterModel(this, new BlockTextures(UltimateTexture.createRegion(2, 1)));
		this.material = Material.WATER;
		this.boundingBoxes.add(new BoundingBox(MIN.setZero(), MAX.set(1, 0.85f, 1)));
	}
}