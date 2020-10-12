package com.travall.game.utils;

import static com.travall.game.utils.LightUtil.*;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;
import com.travall.game.blocks.BlocksList;
import com.travall.game.world.World;

public class FloodLight {
	/** LightNode pool. */
	private static final Pool<LightNode> POOL1 = new Pool<LightNode>(64) {
		protected LightNode newObject() {
			return new LightNode();
		}
	};
	/** LightDelNode pool. */
	private static final Pool<LightDelNode> POOL2 = new Pool<LightDelNode>(64) {
		protected LightDelNode newObject() {
			return new LightDelNode();
		}
	};
	
	private static final Queue<LightNode>    srclightQue    = new Queue<LightNode>(64);
	private static final Queue<LightDelNode> srclightDelQue = new Queue<LightDelNode>(64);
	
	private final World world;
	
	public FloodLight(World world) {
		this.world = world;
	}
	
	public void newSrclightAt(int x, int y, int z) {
		srclightQue.addLast(POOL1.obtain().set(x, y, z));
	}
	
	public void delSrclightAt(int x, int y, int z) {
		srclightDelQue.addLast(POOL2.obtain().set(x, y, z, toSrcLight(world.getLight(x, y, z))));
	}
	
	public void fillSrclight() {
		final short[][][] blocks = world.blocks;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		
		while(srclightQue.notEmpty()) {
			// get the first node from the queue.
			LightNode node = srclightQue.removeFirst();
			
			// Cashes position for quick access.
			int x = node.x;
			int y = node.y;
			int z = node.z;
			
			// Set the chunk dirty.
			world.setMeshDirtyShellAt(x, y, z);
			
			// Get the light value from lightMap at current position
			int lightLevel = toSrcLight(world.getLight(x, y, z));
			
			if (y+1 < height)
			if (!BlocksList.get(blocks[x][y+1][z]).getMaterial().canBlockLights() && toSrcLight(world.getLight(x, y+1, z))+2 <= lightLevel) {
				world.setSrcLight(x, y+1, z, lightLevel-1);
				srclightQue.addLast(POOL1.obtain().set(x, y+1, z));
			}
			if (y-1 >= 0)
			if (!BlocksList.get(blocks[x][y-1][z]).getMaterial().canBlockLights() && toSrcLight(world.getLight(x, y-1, z))+2 <= lightLevel) {
				world.setSrcLight(x, y-1, z, lightLevel-1);
				srclightQue.addLast(POOL1.obtain().set(x, y-1, z));
			}
			if (z-1 >= 0)
			if (!BlocksList.get(blocks[x][y][z-1]).getMaterial().canBlockLights() && toSrcLight(world.getLight(x, y, z-1))+2 <= lightLevel) {
				world.setSrcLight(x, y, z-1, lightLevel-1);
				srclightQue.addLast(POOL1.obtain().set(x, y, z-1));
			}
			if (x-1 >= 0)
			if (!BlocksList.get(blocks[x-1][y][z]).getMaterial().canBlockLights() && toSrcLight(world.getLight(x-1, y, z))+2 <= lightLevel) {
				world.setSrcLight(x-1, y, z, lightLevel-1);
				srclightQue.addLast(POOL1.obtain().set(x-1, y, z));
			}
			if (z+1 < size)
			if (!BlocksList.get(blocks[x][y][z+1]).getMaterial().canBlockLights() && toSrcLight(world.getLight(x, y, z+1))+2 <= lightLevel) {
				world.setSrcLight(x, y, z+1, lightLevel-1);
				srclightQue.addLast(POOL1.obtain().set(x, y, z+1));
			}
			if (x+1 < size)
			if (!BlocksList.get(blocks[x+1][y][z]).getMaterial().canBlockLights() && toSrcLight(world.getLight(x+1, y, z))+2 <= lightLevel) {
				world.setSrcLight(x+1, y, z, lightLevel-1);
				srclightQue.addLast(POOL1.obtain().set(x+1, y, z));
			}
			
			POOL1.free(node);
		}
	}
	
	public void defillSrclight() {
		final int height = World.mapHeight;
		final int size = World.mapSize;
		int neighborLevel;
		
		while(srclightDelQue.notEmpty()) {
			LightDelNode node = srclightDelQue.removeFirst();
			
			// Cashes position for quick access.
			int x = node.x;
			int y = node.y;
			int z = node.z;
			int lightLevel = node.val;
			
			// Set the chunk dirty.
			world.setMeshDirtyShellAt(x, y, z);
			
			if (y+1 < height) {
				neighborLevel = toSrcLight(world.getLight(x, y+1, z));
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y+1, z, 0);
					srclightDelQue.addLast(POOL2.obtain().set(x, y+1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(POOL1.obtain().set(x, y+1, z));
		        }	
			}
			if (y-1 >= 0) {
				neighborLevel = toSrcLight(world.getLight(x, y-1, z));
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y-1, z, 0);
					srclightDelQue.addLast(POOL2.obtain().set(x, y-1, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(POOL1.obtain().set(x, y-1, z));
		        }	
			}
			if (z-1 >= 0) {
				neighborLevel = toSrcLight(world.getLight(x, y, z-1));
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y, z-1, 0);
					srclightDelQue.addLast(POOL2.obtain().set(x, y, z-1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(POOL1.obtain().set(x, y, z-1));
		        }	
			}
			if (x-1 >= 0) {
				neighborLevel = toSrcLight(world.getLight(x-1, y, z));
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x-1, y, z, 0);
					srclightDelQue.addLast(POOL2.obtain().set(x-1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(POOL1.obtain().set(x-1, y, z));
		        }	
			}
			if (z+1 < size) {
				neighborLevel = toSrcLight(world.getLight(x, y, z+1));
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x, y, z+1, 0);
					srclightDelQue.addLast(POOL2.obtain().set(x, y, z+1, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(POOL1.obtain().set(x, y, z+1));
		        }	
			}
			if (x+1 < size) {
				neighborLevel = toSrcLight(world.getLight(x+1, y, z));
				if (neighborLevel != 0 && neighborLevel < lightLevel) {
					world.setSrcLight(x+1, y, z, 0);
					srclightDelQue.addLast(POOL2.obtain().set(x+1, y, z, neighborLevel));
		        } else if (neighborLevel >= lightLevel) {
		        	srclightQue.addLast(POOL1.obtain().set(x+1, y, z));
		        }	
			}
			
			POOL2.free(node);
		}
	}
	
	public static class LightNode {
		public int x, y, z;
		
		private LightNode() {}
		
		public LightNode set(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}
	}
	
	
	public static class LightDelNode {
		public int x, y, z;
		public int val;
		
		public LightDelNode set(int x, int y, int z, int val) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.val = val;
			return this;
		}
	}
}
