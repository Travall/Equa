package com.travall.game.blocks;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.DiagonalModel;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.ui.actors.BlockSeletion;

public class Shrub extends Block {

	public Shrub(int blockID) {
		super(blockID);
		this.model = new DiagonalModel(this, UltimateTexture.createRegion(13, 1));
		this.boundingBoxes.add(new BoundingBox(MIN.setZero(), MAX.set(1, 1, 1)));
		this.material = Material.DIAGONAL;
		BlockSeletion.add(this);
	}
}
