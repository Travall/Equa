package com.travall.game.blocks.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.Slab;
import com.travall.game.blocks.Water;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class WaterModel implements IBlockModel {

	private final TextureRegion texture;
	private final Water block;

	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;

	public WaterModel(Water block, TextureRegion texture) {
		this.texture = texture;
		this.block = block;
		
		quad1 = new QuadNode();
		quad1.v1.setPos(1, 0.85f, 0);
		quad1.v2.setPos(0, 0.85f, 0);
		quad1.v3.setPos(0, 0.85f, 1);
		quad1.v4.setPos(1, 0.85f, 1);
		quad1.face = Facing.UP;
		quad1.isInside = true;
		quad1.region.setRegion(texture);
		
		quad2 = new QuadNode();
		quad2.v1.setPos(0, 0, 0);
		quad2.v2.setPos(1, 0, 0);
		quad2.v3.setPos(1, 0, 1);
		quad2.v4.setPos(0, 0, 1);
		quad2.face = Facing.DOWN;
		quad2.region.setRegion(texture);
		
		TextureRegion haftTexture = new TextureRegion(texture, 0, 0, 16, 8);
		
		quad3 = new QuadNode();
		quad3.v1.setPos(1, 0, 1);
		quad3.v2.setPos(1, 0.85f, 1);
		quad3.v3.setPos(0, 0.85f, 1);
		quad3.v4.setPos(0, 0, 1);
		quad3.face = Facing.NORTH;
		quad3.region.setRegion(haftTexture);
		
		quad4 = new QuadNode();
		quad4.v1.setPos(1, 0, 0);
		quad4.v2.setPos(1, 0.85f, 0);
		quad4.v3.setPos(1, 0.85f, 1);
		quad4.v4.setPos(1, 0, 1);
		quad4.face = Facing.EAST;
		quad4.region.setRegion(haftTexture);
		
		quad5 = new QuadNode();
		quad5.v1.setPos(0, 0, 0);
		quad5.v2.setPos(0, 0.85f, 0);
		quad5.v3.setPos(1, 0.85f, 0);
		quad5.v4.setPos(1, 0, 0);
		quad5.face = Facing.SOUTH;
		quad5.region.setRegion(haftTexture);
		
		quad6 = new QuadNode();
		quad6.v1.setPos(0, 0, 1);
		quad6.v2.setPos(0, 0.85f, 1);
		quad6.v3.setPos(0, 0.85f, 0);
		quad6.v4.setPos(0, 0, 0);
		quad6.face = Facing.WEST;
		quad6.region.setRegion(haftTexture);
	}
	
	private final BlockPos second = new BlockPos();

	@Override
	public void build(QuadBuilder builder, BlockPos position) {
		int x = position.x, y = position.y, z = position.z;
		final Block block = this.block;
		if (block.canAddFace(position, second.set(x, y+1, z), Facing.UP))    quad1.rect(builder, position);
		if (block.canAddFace(position, second.set(x, y-1, z), Facing.DOWN))  quad2.rect(builder, position);
		if (block.canAddFace(position, second.set(x, y, z+1), Facing.NORTH)) quad3.rect(builder, position);
		if (block.canAddFace(position, second.set(x+1, y, z), Facing.EAST))  quad4.rect(builder, position);
		if (block.canAddFace(position, second.set(x, y, z-1), Facing.SOUTH)) quad5.rect(builder, position);
		if (block.canAddFace(position, second.set(x-1, y, z), Facing.WEST))  quad6.rect(builder, position);
	}

	@Override
	public TextureRegion getDefaultTexture() {
		return texture;
	}
}
