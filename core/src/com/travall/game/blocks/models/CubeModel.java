package com.travall.game.blocks.models;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.blocks.Block;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.ArrayDir;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class CubeModel implements IBlockModel {
	
	private final ArrayDir<BlockTextures> textures;
	private final Block block;
	
	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;
	
	public CubeModel(Block block, BlockTextures textures) {
		this(block, new ArrayDir<>(BlockTextures.class, 1).put(0, textures));
	}
	
	public CubeModel(Block block, ArrayDir<BlockTextures> textures) {
		this.textures = textures;
		this.block = block;
		
		quad1 = new QuadNode();
		quad1.p1.set(1, 1, 0);
		quad1.p2.set(0, 1, 0);
		quad1.p3.set(0, 1, 1);
		quad1.p4.set(1, 1, 1);
		quad1.face = Facing.UP;
		
		quad2 = new QuadNode();
		quad2.p1.set(0, 0, 0);
		quad2.p2.set(1, 0, 0);
		quad2.p3.set(1, 0, 1);
		quad2.p4.set(0, 0, 1);
		quad2.face = Facing.DOWN;
		
		quad3 = new QuadNode();
		quad3.p1.set(0, 0, 0);
		quad3.p2.set(0, 1, 0);
		quad3.p3.set(1, 1, 0);
		quad3.p4.set(1, 0, 0);
		quad3.face = Facing.NORTH;
		
		quad4 = new QuadNode();
		quad4.p1.set(1, 0, 0);
		quad4.p2.set(1, 1, 0);
		quad4.p3.set(1, 1, 1);
		quad4.p4.set(1, 0, 1);
		quad4.face = Facing.EAST;
		
		quad5 = new QuadNode();
		quad5.p1.set(1, 0, 1);
		quad5.p2.set(1, 1, 1);
		quad5.p3.set(0, 1, 1);
		quad5.p4.set(0, 0, 1);
		quad5.face = Facing.SOUTH;
		
		quad6 = new QuadNode();
		quad6.p1.set(0, 0, 1);
		quad6.p2.set(0, 1, 1);
		quad6.p3.set(0, 1, 0);
		quad6.p4.set(0, 0, 0);
		quad6.face = Facing.WEST;
	}

	private final BlockPos second = new BlockPos();

	@Override
	public void build(QuadBuilder builder, BlockPos position) {
		final int x = position.x, y = position.y, z = position.z;
		final Block block = this.block;
		
		final BlockTextures texs = textures.get(block.getType(position));
		
		if (block.canAddFace(position, second.set(x, y+1, z), Facing.UP))    {
			quad1.region = texs.top;
			quad1.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x, y-1, z), Facing.DOWN))  {
			quad2.region = texs.bottom;
			quad2.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x, y, z-1), Facing.NORTH)) {
			quad3.region = texs.north;
			quad3.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x+1, y, z), Facing.EAST))  {
			quad4.region = texs.east;
			quad4.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x, y, z+1), Facing.SOUTH)) {
			quad5.region = texs.south;
			quad5.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x-1, y, z), Facing.WEST))  {
			quad6.region = texs.west;
			quad6.rect(builder, position);
		}
	}

	@Override
	public TextureRegion getDefaultTexture(BlockPos pos, int data) {
		if (pos != null) {
			return textures.get(world.getBlock(pos).getType(pos)).north;
		}
		return textures.get(data).north;
	}
}
