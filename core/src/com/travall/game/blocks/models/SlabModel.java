package com.travall.game.blocks.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.Slab;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class SlabModel implements IBlockModel {

	private final TextureRegion texture;
	private final Slab block;
	
	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;
	
	public SlabModel(Slab block, TextureRegion texture) {
		this.texture = texture;
		this.block = block;
		
		quad1 = new QuadNode();
		quad1.p1.set(1, 0.5f, 0);
		quad1.p2.set(0, 0.5f, 0);
		quad1.p3.set(0, 0.5f, 1);
		quad1.p4.set(1, 0.5f, 1);
		quad1.face = Facing.UP;
		quad1.isInside = true;
		quad1.region.setRegion(texture);
		
		quad2 = new QuadNode();
		quad2.p1.set(0, 0, 0);
		quad2.p2.set(1, 0, 0);
		quad2.p3.set(1, 0, 1);
		quad2.p4.set(0, 0, 1);
		quad2.face = Facing.DOWN;
		quad2.region.setRegion(texture);
		
		TextureRegion haftTexture = new TextureRegion(texture, 0, 0, 16, 8);
		
		quad3 = new QuadNode();
		quad3.p1.set(0, 0, 0);
		quad3.p2.set(0, 0.5f, 0);
		quad3.p3.set(1, 0.5f, 0);
		quad3.p4.set(1, 0, 0);
		quad3.face = Facing.NORTH;
		quad3.region.setRegion(haftTexture);
		
		quad4 = new QuadNode();
		quad4.p1.set(1, 0, 0);
		quad4.p2.set(1, 0.5f, 0);
		quad4.p3.set(1, 0.5f, 1);
		quad4.p4.set(1, 0, 1);
		quad4.face = Facing.EAST;
		quad4.region.setRegion(haftTexture);
		
		quad5 = new QuadNode();
		quad5.p1.set(1, 0, 1);
		quad5.p2.set(1, 0.5f, 1);
		quad5.p3.set(0, 0.5f, 1);
		quad5.p4.set(0, 0, 1);
		quad5.face = Facing.SOUTH;
		quad5.region.setRegion(haftTexture);
		
		quad6 = new QuadNode();
		quad6.p1.set(0, 0, 1);
		quad6.p2.set(0, 0.5f, 1);
		quad6.p3.set(0, 0.5f, 0);
		quad6.p4.set(0, 0, 0);
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
		if (block.canAddFace(position, second.set(x, y, z-1), Facing.NORTH)) quad3.rect(builder, position);
		if (block.canAddFace(position, second.set(x+1, y, z), Facing.EAST))  quad4.rect(builder, position);
		if (block.canAddFace(position, second.set(x, y, z+1), Facing.SOUTH)) quad5.rect(builder, position);
		if (block.canAddFace(position, second.set(x-1, y, z), Facing.WEST))  quad6.rect(builder, position);
	}

	@Override
	public TextureRegion getDefaultTexture() {
		return texture;
	}
}
