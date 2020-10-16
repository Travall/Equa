package com.travall.game.blocks.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.blocks.Block;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class DiagonalModel implements IBlockModel {

	private final TextureRegion texture;

	private final QuadNode quad1, quad2, quad3, quad4;

	public DiagonalModel(Block block, TextureRegion texture) {
		this.texture = texture;
		
		quad1 = new QuadNode();
		quad1.v1.setPos(0, 0, 0);
		quad1.v2.setPos(0, 1, 0);
		quad1.v3.setPos(1, 1, 1);
		quad1.v4.setPos(1, 0, 1);
		quad1.face = Facing.UP;
		quad1.region.setRegion(texture);
		
		quad2 = new QuadNode();
		quad2.v1.setPos(1, 0, 0);
		quad2.v2.setPos(1, 1, 0);
		quad2.v3.setPos(0, 1, 1);
		quad2.v4.setPos(0, 0, 1);
		quad2.face = Facing.UP;
		quad2.region.setRegion(texture);

		quad3 = new QuadNode();
		quad3.v1.setPos(0, 0, 1);
		quad3.v2.setPos(0, 1, 1);
		quad3.v3.setPos(1, 1, 0);
		quad3.v4.setPos(1, 0, 0);
		quad3.face = Facing.UP;
		quad3.region.setRegion(texture);

		quad4 = new QuadNode();
		quad4.v1.setPos(1, 0, 1);
		quad4.v2.setPos(1, 1, 1);
		quad4.v3.setPos(0, 1, 0);
		quad4.v4.setPos(0, 0, 0);
		quad4.face = Facing.UP;
		quad4.region.setRegion(texture);
	}
	
	@Override
	public void build(QuadBuilder builder, BlockPos position) {
		quad1.rect(builder, position);
		quad2.rect(builder, position);
		quad3.rect(builder, position);
		quad4.rect(builder, position);
	}

	@Override
	public TextureRegion getDefaultTexture() {
		return texture;
	}
}
