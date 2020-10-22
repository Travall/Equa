package com.travall.game.blocks.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.blocks.Block;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class CactusModel implements IBlockModel {

	private final TextureRegion texture;
	private final Block block;

	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;

	public CactusModel(Block block, BlockTextures textures) {
		this.texture = textures.north;
		this.block = block;

		float width = 12/16f;
		float height = 1f;
		float length = 12/16f;

		quad1 = new QuadNode();
		quad1.p1.set(1, 1, 0);
		quad1.p2.set(0, 1, 0);
		quad1.p3.set(0, 1, 1);
		quad1.p4.set(1, 1, 1);
		quad1.face = Facing.UP;
		quad1.region.setRegion(textures.top);

		quad2 = new QuadNode();
		quad2.p1.set(0, 0, 0);
		quad2.p2.set(1, 0, 0);
		quad2.p3.set(1, 0, 1);
		quad2.p4.set(0, 0, 1);
		quad2.face = Facing.DOWN;
		quad2.region.setRegion(textures.bottom);
		
		quad3 = new QuadNode();
		quad3.p1.set(0, 0, 0);
		quad3.p2.set(0, 1, 0);
		quad3.p3.set(1, 1, 0);
		quad3.p4.set(1, 0, 0);
		quad3.face = Facing.NORTH;
		quad3.region.setRegion(textures.south);

		quad4 = new QuadNode();
		quad4.p1.set(1, 0, 0);
		quad4.p2.set(1, 1, 0);
		quad4.p3.set(1, 1, 1);
		quad4.p4.set(1, 0, 1);
		quad4.face = Facing.EAST;
		quad4.region.setRegion(textures.south);

		quad5 = new QuadNode();
		quad5.p1.set(1, 0, 1);
		quad5.p2.set(1, 1, 1);
		quad5.p3.set(0, 1, 1);
		quad5.p4.set(0, 0, 1);
		quad5.face = Facing.SOUTH;
		quad5.region.setRegion(textures.south);

		quad6 = new QuadNode();
		quad6.p1.set(0, 0, 1);
		quad6.p2.set(0, 1, 1);
		quad6.p3.set(0, 1, 0);
		quad6.p4.set(0, 0, 0);
		quad6.face = Facing.WEST;
		quad6.region.setRegion(textures.west);

//		quad3.mul(1,1,14/16f);
//		quad4.mul(14/16f,1,1);
//		quad5.add(0,0,2/16f);
//		quad6.add(2/16f,0,0);

		quad1.scl(1,height,1);
		quad3.add(0,0,8/16f - (length / 2));
		quad4.scl(8/16f + (width / 2),1,1);
		quad5.scl(1,1,8/16f + (width / 2));
		quad6.add(8/16f - (length / 2),0,0);
	}
	
	private final BlockPos second = new BlockPos();

	@Override
	public void build(QuadBuilder builder, BlockPos position) {
		int x = position.x, y = position.y, z = position.z;
		final Block block = this.block;
		if (block.canAddFace(position, second.set(x, y+1, z), Facing.UP))    quad1.rect(builder, position);
		if (block.canAddFace(position, second.set(x, y-1, z), Facing.DOWN))  quad2.rect(builder, position);
		quad3.rect(builder, position);
		quad4.rect(builder, position);
		quad5.rect(builder, position);
		quad6.rect(builder, position);
	}

	@Override
	public TextureRegion getDefaultTexture() {
		return texture;
	}
}
