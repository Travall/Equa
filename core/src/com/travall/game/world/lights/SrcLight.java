package com.travall.game.world.lights;

import static com.travall.game.world.World.world;
import static com.travall.game.utils.BlockUtils.toSrcLight;

import com.badlogic.gdx.utils.Queue;
import com.travall.game.blocks.BlocksList;
import com.travall.game.world.World;

final class SrcLight {	
	private static final Queue<LightNode>    srclightQue    = new Queue<LightNode>(64);
	private static final Queue<LightDelNode> srclightDelQue = new Queue<LightDelNode>(64);
	
	public static void newSrclightAt(int x, int y, int z) {
		srclightQue.addLast(LightNode.POOL.obtain().set(x, y, z));
	}
	
	public static void delSrclightAt(int x, int y, int z) {
		srclightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y, z, (byte)toSrcLight(world.data[x][y][z])));
	}
	
	public static void fillSrclight() {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		
		while(srclightQue.notEmpty()) {
			// get the first node from the queue.
			final LightNode node = srclightQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			
			// Set the chunk dirty.
			world.setMeshDirtyShellAt(x, y, z);
			
			// Get the light value from lightMap at current position
			final int lightLevel = toSrcLight(data[x][y][z]);
			
			if (y+1 < height)
			if (!BlocksList.get(data[x][y+1][z]).getMaterial().canBlockLights() && toSrcLight(data[x][y+1][z])+2 <= lightLevel) {
				world.setSrcLight(x, y+1, z, lightLevel-1);
				srclightQue.addLast(LightNode.POOL.obtain().set(x, y+1, z));
			}
			if (y-1 >= 0)
			if (!BlocksList.get(data[x][y-1][z]).getMaterial().canBlockLights() && toSrcLight(data[x][y-1][z])+2 <= lightLevel) {
				world.setSrcLight(x, y-1, z, lightLevel-1);
				srclightQue.addLast(LightNode.POOL.obtain().set(x, y-1, z));
			}
			if (z-1 >= 0)
			if (!BlocksList.get(data[x][y][z-1]).getMaterial().canBlockLights() && toSrcLight(data[x][y][z-1])+2 <= lightLevel) {
				world.setSrcLight(x, y, z-1, lightLevel-1);
				srclightQue.addLast(LightNode.POOL.obtain().set(x, y, z-1));
			}
			if (x-1 >= 0)
			if (!BlocksList.get(data[x-1][y][z]).getMaterial().canBlockLights() && toSrcLight(data[x-1][y][z])+2 <= lightLevel) {
				world.setSrcLight(x-1, y, z, lightLevel-1);
				srclightQue.addLast(LightNode.POOL.obtain().set(x-1, y, z));
			}
			if (z+1 < size)
			if (!BlocksList.get(data[x][y][z+1]).getMaterial().canBlockLights() && toSrcLight(data[x][y][z+1])+2 <= lightLevel) {
				world.setSrcLight(x, y, z+1, lightLevel-1);
				srclightQue.addLast(LightNode.POOL.obtain().set(x, y, z+1));
			}
			if (x+1 < size)
			if (!BlocksList.get(data[x+1][y][z]).getMaterial().canBlockLights() && toSrcLight(data[x+1][y][z])+2 <= lightLevel) {
				world.setSrcLight(x+1, y, z, lightLevel-1);
				srclightQue.addLast(LightNode.POOL.obtain().set(x+1, y, z));
			}
			
			LightNode.POOL.free(node);
		}
	}
	
	public static void defillSrclight() {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		byte neighborLevel;
		
		while(srclightDelQue.notEmpty()) {
			LightDelNode node = srclightDelQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			final byte lightLevel = node.val;
			
			// Set the chunk dirty.
			world.setMeshDirtyShellAt(x, y, z);
			
			if (y+1 < height) {
				neighborLevel = (byte)toSrcLight(data[x][y+1][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y+1, z, 0);
					srclightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y+1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(LightNode.POOL.obtain().set(x, y+1, z));
		        }	
			}
			if (y-1 >= 0) {
				neighborLevel = (byte)toSrcLight(data[x][y-1][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y-1, z, 0);
					srclightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y-1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(LightNode.POOL.obtain().set(x, y-1, z));
		        }	
			}
			if (z-1 >= 0) {
				neighborLevel = (byte)toSrcLight(data[x][y][z-1]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y, z-1, 0);
					srclightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y, z-1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(LightNode.POOL.obtain().set(x, y, z-1));
		        }	
			}
			if (x-1 >= 0) {
				neighborLevel = (byte)toSrcLight(data[x-1][y][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x-1, y, z, 0);
					srclightDelQue.addLast(LightDelNode.POOL.obtain().set(x-1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(LightNode.POOL.obtain().set(x-1, y, z));
		        }	
			}
			if (z+1 < size) {
				neighborLevel = (byte)toSrcLight(data[x][y][z+1]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y, z+1, 0);
					srclightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y, z+1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(LightNode.POOL.obtain().set(x, y, z+1));
		        }	
			}
			if (x+1 < size) {
				neighborLevel = (byte)toSrcLight(data[x+1][y][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x+1, y, z, 0);
					srclightDelQue.addLast(LightDelNode.POOL.obtain().set(x+1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(LightNode.POOL.obtain().set(x+1, y, z));
		        }	
			}
			
			LightDelNode.POOL.free(node);
		}
	}
}
