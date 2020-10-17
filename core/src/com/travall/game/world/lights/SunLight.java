package com.travall.game.world.lights;

import static com.travall.game.utils.BlockUtils.toSunLight;
import static com.travall.game.world.World.world;

import com.badlogic.gdx.utils.Queue;
import com.travall.game.blocks.BlocksList;
import com.travall.game.world.World;

final class SunLight {
	private static final Queue<LightNode> sunlightQue = new Queue<LightNode>(256);
	private static final Queue<LightDelNode> sunlightDelQue = new Queue<LightDelNode>(128);
	
	public static void newSunlightAt(int x, int y, int z) {
		sunlightQue.addLast(LightNode.POOL.obtain().set(x, y, z));
	}
	
	public static void delSunlightAt(int x, int y, int z) {
		sunlightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y, z, (byte)toSunLight(world.data[x][y][z])));
	}
	
	public static void fillSunlight() {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		
		while(sunlightQue.notEmpty()) {
			// get the first node from the queue.
			final LightNode node = sunlightQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			
			// Set the chunk dirty.
			world.setMeshDirtyShellAt(x, y, z);
			
			// Get the light value from lightMap at current position
			final int lightLevel = toSunLight(data[x][y][z]);
			
			if (y+1 < height)
			if (!BlocksList.get(data[x][y+1][z]).getMaterial().canBlockLights() && toSunLight(data[x][y+1][z])+2 <= lightLevel) {
				world.setSunLight(x, y+1, z, lightLevel-1);
				sunlightQue.addLast(LightNode.POOL.obtain().set(x, y+1, z));
			}
			if (y-1 >= 0)
			if (!BlocksList.get(data[x][y-1][z]).getMaterial().canBlockLights() && toSunLight(data[x][y-1][z])+2 <= lightLevel) {
				world.setSunLight(x, y-1, z, lightLevel-1);
				sunlightQue.addLast(LightNode.POOL.obtain().set(x, y-1, z));
			}
			if (z-1 >= 0)
			if (!BlocksList.get(data[x][y][z-1]).getMaterial().canBlockLights() && toSunLight(data[x][y][z-1])+2 <= lightLevel) {
				world.setSunLight(x, y, z-1, lightLevel-1);
				sunlightQue.addLast(LightNode.POOL.obtain().set(x, y, z-1));
			}
			if (x-1 >= 0)
			if (!BlocksList.get(data[x-1][y][z]).getMaterial().canBlockLights() && toSunLight(data[x-1][y][z])+2 <= lightLevel) {
				world.setSunLight(x-1, y, z, lightLevel-1);
				sunlightQue.addLast(LightNode.POOL.obtain().set(x-1, y, z));
			}
			if (z+1 < size)
			if (!BlocksList.get(data[x][y][z+1]).getMaterial().canBlockLights() && toSunLight(data[x][y][z+1])+2 <= lightLevel) {
				world.setSunLight(x, y, z+1, lightLevel-1);
				sunlightQue.addLast(LightNode.POOL.obtain().set(x, y, z+1));
			}
			if (x+1 < size)
			if (!BlocksList.get(data[x+1][y][z]).getMaterial().canBlockLights() && toSunLight(data[x+1][y][z])+2 <= lightLevel) {
				world.setSunLight(x+1, y, z, lightLevel-1);
				sunlightQue.addLast(LightNode.POOL.obtain().set(x+1, y, z));
			}
			
			LightNode.POOL.free(node);
		}
	}
	
	public static void defillSunlight() {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		byte neighborLevel;
		
		while(sunlightDelQue.notEmpty()) {
			LightDelNode node = sunlightDelQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			final byte lightLevel = node.val;
			
			// Set the chunk dirty.
			world.setMeshDirtyShellAt(x, y, z);
			
			if (y+1 < height) {
				neighborLevel = (byte)toSunLight(data[x][y+1][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x, y+1, z, 0);
					sunlightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y+1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(LightNode.POOL.obtain().set(x, y+1, z));
		        }	
			}
			if (y-1 >= 0) {
				neighborLevel = (byte)toSunLight(data[x][y-1][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x, y-1, z, 0);
					sunlightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y-1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(LightNode.POOL.obtain().set(x, y-1, z));
		        }	
			}
			if (z-1 >= 0) {
				neighborLevel = (byte)toSunLight(data[x][y][z-1]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x, y, z-1, 0);
					sunlightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y, z-1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(LightNode.POOL.obtain().set(x, y, z-1));
		        }	
			}
			if (x-1 >= 0) {
				neighborLevel = (byte)toSunLight(data[x-1][y][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x-1, y, z, 0);
					sunlightDelQue.addLast(LightDelNode.POOL.obtain().set(x-1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(LightNode.POOL.obtain().set(x-1, y, z));
		        }	
			}
			if (z+1 < size) {
				neighborLevel = (byte)toSunLight(data[x][y][z+1]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x, y, z+1, 0);
					sunlightDelQue.addLast(LightDelNode.POOL.obtain().set(x, y, z+1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(LightNode.POOL.obtain().set(x, y, z+1));
		        }	
			}
			if (x+1 < size) {
				neighborLevel = (byte)toSunLight(data[x+1][y][z]);
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSunLight(x+1, y, z, 0);
					sunlightDelQue.addLast(LightDelNode.POOL.obtain().set(x+1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	sunlightQue.addLast(LightNode.POOL.obtain().set(x+1, y, z));
		        }	
			}
			
			LightDelNode.POOL.free(node);
		}
	}
}
