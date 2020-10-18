package com.travall.game.blocks.models;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.blocks.Water;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class WaterModel implements IBlockModel {

	private final TextureRegion texture;
	private final Block block;

	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;
	
	private static final float lowWater = 14f/16f;

	public WaterModel(Water block, TextureRegion texture) {
		this.texture = texture;
		this.block = block;

		quad1 = new QuadNode();
		quad1.p1.set(1, 1, 0);
		quad1.p2.set(0, 1, 0);
		quad1.p3.set(0, 1, 1);
		quad1.p4.set(1, 1, 1);
		quad1.face = Facing.UP;
		quad1.region.setRegion(texture);

		quad2 = new QuadNode();
		quad2.p1.set(0, 0, 0);
		quad2.p2.set(1, 0, 0);
		quad2.p3.set(1, 0, 1);
		quad2.p4.set(0, 0, 1);
		quad2.face = Facing.DOWN;
		quad2.region.setRegion(texture);
		
		final TextureRegion haftTexture = new TextureRegion(texture, 0, 0, 16, 14);

		quad3 = new QuadNode();
		quad3.p1.set(1, 0, 1);
		quad3.p2.set(1, 1, 1);
		quad3.p3.set(0, 1, 1);
		quad3.p4.set(0, 0, 1);
		quad3.face = Facing.NORTH;
		quad3.region.setRegion(haftTexture);

		quad4 = new QuadNode();
		quad4.p1.set(1, 0, 0);
		quad4.p2.set(1, 1, 0);
		quad4.p3.set(1, 1, 1);
		quad4.p4.set(1, 0, 1);
		quad4.face = Facing.EAST;
		quad4.region.setRegion(haftTexture);

		quad5 = new QuadNode();
		quad5.p1.set(0, 0, 0);
		quad5.p2.set(0, 1, 0);
		quad5.p3.set(1, 1, 0);
		quad5.p4.set(1, 0, 0);
		quad5.face = Facing.SOUTH;
		quad5.region.setRegion(haftTexture);

		quad6 = new QuadNode();
		quad6.p1.set(0, 0, 1);
		quad6.p2.set(0, 1, 1);
		quad6.p3.set(0, 1, 0);
		quad6.p4.set(0, 0, 0);
		quad6.face = Facing.WEST;
		quad6.region.setRegion(haftTexture);
	}

	private final BlockPos second = new BlockPos();

	@Override
	public void build(QuadBuilder builder, BlockPos position) {
		int x = position.x, y = position.y, z = position.z;
		final Block block = this.block;
		final boolean isWaterUp = world.getBlock(second.set(x, y+1, z)) == BlocksList.WATER;
		if (block.canAddFace(position, second.set(x, y+1, z), Facing.UP)) {
			quad1.p1.y = isWaterUp ? 1f : lowWater;
			quad1.p2.y = isWaterUp ? 1f : lowWater;
			quad1.p3.y = isWaterUp ? 1f : lowWater;
			quad1.p4.y = isWaterUp ? 1f : lowWater;
			quad1.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x, y-1, z), Facing.DOWN)) {
			quad2.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x, y, z+1), Facing.NORTH)) {
			quad3.p2.y = isWaterUp ? 1f : lowWater;
			quad3.p3.y = isWaterUp ? 1f : lowWater;
			quad3.region.setRegionHeight(isWaterUp ? 16 : 14);
			quad3.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x+1, y, z), Facing.EAST)) {
			quad4.p2.y = isWaterUp ? 1f : lowWater;
			quad4.p3.y = isWaterUp ? 1f : lowWater;
			quad4.region.setRegionHeight(isWaterUp ? 16 : 14);
			quad4.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x, y, z-1), Facing.SOUTH)) {
			quad5.p2.y = isWaterUp ? 1f : lowWater;
			quad5.p3.y = isWaterUp ? 1f : lowWater;
			quad5.region.setRegionHeight(isWaterUp ? 16 : 14);
			quad5.rect(builder, position);
		}
		if (block.canAddFace(position, second.set(x-1, y, z), Facing.WEST)) {
			quad6.p2.y = isWaterUp ? 1f : lowWater;
			quad6.p3.y = isWaterUp ? 1f : lowWater;
			quad6.region.setRegionHeight(isWaterUp ? 16 : 14);
			quad6.rect(builder, position);
		}
	}

	@Override
	public TextureRegion getDefaultTexture() {
		return texture;
	}
	
	private static final Array<BoundingBox> CUBE = new Array<>(1);
	private static final Array<BoundingBox> HAFT = new Array<>(1);
	
	static {
		CUBE.add(new BoundingBox(MIN.setZero(), MAX.set(1, 1, 1)));
		HAFT.add(new BoundingBox(MIN.setZero(), MAX.set(1, lowWater, 1)));
	}
	
	@Override
	public Array<BoundingBox> getBoundingBoxes(BlockPos pos) {
		return world.getBlock(second.set(pos.x, pos.y+1, pos.z)) == BlocksList.WATER ? CUBE : HAFT;
	}
}
