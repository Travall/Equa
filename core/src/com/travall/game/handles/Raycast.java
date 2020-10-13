package com.travall.game.handles;

import static com.badlogic.gdx.math.MathUtils.floor;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.Block;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;
import com.travall.game.world.World;

public class Raycast {
	private static final float LENGHT = 32.0f;
	private static final float STEPS  = 0.01f;
	private static final RayInfo info = new RayInfo();
	private static final Vector3 pos  = new Vector3();
	private static final Vector3 nor  = new Vector3();
	private static final BlockPos offset = new BlockPos();
	
	private static final Ray ray = new Ray();
	private static final Vector3 intersect = new Vector3();
	private static final BoundingBox box = new BoundingBox();
	private static final BlockPos inBlock = new BlockPos();
	
	
	public static RayInfo Fastcast(Camera cam, World map) {
		nor.set(cam.direction).scl(STEPS);
		
		final Vector3 rayPos = cam.position;
		offset.set(floor(rayPos.x), floor(rayPos.y), floor(rayPos.z));
		
		// if Block is inside the player, then cast that block
		if (!map.isAirBlock(offset.x, offset.y, offset.z)) {
			info.in.set(offset);
			info.out.set(offset);
			return info;
		}
		
		pos.set(rayPos).sub(offset.x, offset.y, offset.z);
		
		int x  = 0, y  = 0, z  = 0; // Current integer position.
		int lx = 0, ly = 0, lz = 0; // Last integer position.
		
		for (float i = 0; i < LENGHT; i += STEPS) {
			pos.add(nor);
			if ((y = floor(pos.y)) == ly && (x = floor(pos.x)) == lx && (z = floor(pos.z)) == lz) continue;
			
			if (!map.isAirBlock(x+offset.x, y+offset.y, z+offset.z)) {
				lx = x; ly = y; lz = z;
				continue;
			}
			
			info.in.set(x, y, z).add(offset);
			info.out.set(lx, ly, lz).add(offset);
			return info;
		}
		
		return null;
	}
	
	public static RayInfo shot(Camera cam, World world) {
		nor.set(cam.direction).scl(STEPS);

		final Vector3 rayPos = cam.position;
		offset.set(floor(rayPos.x), floor(rayPos.y), floor(rayPos.z));
		pos.set(rayPos).sub(offset.x, offset.y, offset.z);
		
		ray.origin.set(pos);
		ray.set(pos, cam.direction);

		int x = 0, y = 0, z = 0; // Current integer position.
		int lx = 0, ly = 0, lz = 0; // Last integer position.

		for (float i = 0; i < LENGHT; i += STEPS) {
			pos.add(nor);
			if ((y = floor(pos.y)) == ly && (x = floor(pos.x)) == lx && (z = floor(pos.z)) == lz)
				continue;
			
			final Block block = world.getBlock(inBlock.set(x + offset.x, y + offset.y, z + offset.z));

			if (block.isAir()) {
				lx = x;
				ly = y;
				lz = z;
				continue;
			}
			
			final Array<BoundingBox> boundingBoxes = block.getBoundingBoxes(inBlock);
			
			Facing face = null;
			for (BoundingBox staticBox : boundingBoxes) {
				float closest = 10f;
				box.set(staticBox);
				box.max.add(x, y, z);
				box.min.add(x, y, z);
				if (Intersector.intersectRayBounds(ray, box, intersect)) {
					if (box.max.y - intersect.y < closest) {
						closest = box.max.y - intersect.y;
						face = Facing.UP;
					}
					
					if (-(box.min.y - intersect.y) < closest) {
						closest = -(box.min.y - intersect.y);
						face = Facing.DOWN;
					}
					
					if (box.max.x - intersect.x < closest) {
						closest = box.max.x - intersect.x;
						face = Facing.EAST;
					}
					
					if (-(box.min.x - intersect.x) < closest) {
						closest = -(box.min.x - intersect.x);
						face = Facing.WEST;
					}
					
					if (box.max.z - intersect.z < closest) {
						closest = box.max.z - intersect.z;
						face = Facing.NORTH;
					}
					
					if (-(box.min.z - intersect.z) < closest) {
						closest = -(box.min.z - intersect.z);
						face = Facing.SOUTH;
					}

					if (face != null) {
						info.boxHit = staticBox;
						break;
					}
				}
			}

			if (face == null) {
				if (!block.getMaterial().isFullCube()) {
					lx = x;
					ly = y;
					lz = z;
					continue;
				}
				return null;
			}
			
			info.face = face;
			info.in.set(inBlock);
			info.out.set(inBlock).add(face.offset);
			info.blockHit = block;
			return info;
		}
		
		return null;
	}
	
	public static class RayInfo {
		/** Outside the block. */
		public final BlockPos out = new BlockPos();
		/** Inside the block. */
		public final BlockPos in  = new BlockPos();
		
		public Facing face;
		
		public BoundingBox boxHit;
		public Block blockHit;
	}
}
