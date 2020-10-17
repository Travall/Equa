package com.travall.game.blocks;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.TorchModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;

public class Torch extends Block {

	public Torch(int blockID) {
		super(blockID);
		this.model = new TorchModel(this, new BlockTextures(UltimateTexture.createRegion(14, 2),UltimateTexture.createRegion(14, 1)));
		this.material = Material.TORCH;
		this.boundingBoxes.add(new BoundingBox(MIN.set(7/16f,0,7/16f), MAX.set(9/16f, 10/16f, 9/16f)));
		this.lightLevel = 15;
	}
}
