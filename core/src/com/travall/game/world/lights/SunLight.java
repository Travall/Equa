package com.travall.game.world.lights;

import static com.travall.game.utils.BlockUtils.toSunLight;
import static com.travall.game.world.World.world;

import com.badlogic.gdx.utils.Queue;
import com.travall.game.blocks.BlocksList;
import com.travall.game.world.World;

public class SunLight {
	private static final Queue<LightNode> skylightQue = new Queue<LightNode>(512);
	
	public static void newSunlightAt(int x, int y, int z) {
		skylightQue.addLast(LightNode.POOL.obtain().set(x, y, z));
	}
	
	public static void fillSunlight() {
		final int[][][] data = world.data;
		final int height = World.mapHeight;
		final int size = World.mapSize;
		
		while(skylightQue.notEmpty()) {
			// get the first node from the queue.
			final LightNode node = skylightQue.removeFirst();
			
			// Cashes position for quick access.
			final int x = node.x;
			final int y = node.y;
			final int z = node.z;
			
			// Set the chunk dirty.
			//world.setMeshDirtyShellAt(x, y, z);
			
			// Get the light value from lightMap at current position
			final int lightLevel = toSunLight(data[x][y][z]);
			
			if (y+1 < height)
			if (!BlocksList.get(data[x][y+1][z]).getMaterial().canBlockLights() && toSunLight(data[x][y+1][z])+2 <= lightLevel) {
				world.setSunLight(x, y+1, z, lightLevel-1);
				skylightQue.addLast(LightNode.POOL.obtain().set(x, y+1, z));
			}
			if (y-1 >= 0)
			if (!BlocksList.get(data[x][y-1][z]).getMaterial().canBlockLights() && toSunLight(data[x][y-1][z])+2 <= lightLevel) {
				world.setSunLight(x, y-1, z, lightLevel-1);
				skylightQue.addLast(LightNode.POOL.obtain().set(x, y-1, z));
			}
			if (z-1 >= 0)
			if (!BlocksList.get(data[x][y][z-1]).getMaterial().canBlockLights() && toSunLight(data[x][y][z-1])+2 <= lightLevel) {
				world.setSunLight(x, y, z-1, lightLevel-1);
				skylightQue.addLast(LightNode.POOL.obtain().set(x, y, z-1));
			}
			if (x-1 >= 0)
			if (!BlocksList.get(data[x-1][y][z]).getMaterial().canBlockLights() && toSunLight(data[x-1][y][z])+2 <= lightLevel) {
				world.setSunLight(x-1, y, z, lightLevel-1);
				skylightQue.addLast(LightNode.POOL.obtain().set(x-1, y, z));
			}
			if (z+1 < size)
			if (!BlocksList.get(data[x][y][z+1]).getMaterial().canBlockLights() && toSunLight(data[x][y][z+1])+2 <= lightLevel) {
				world.setSunLight(x, y, z+1, lightLevel-1);
				skylightQue.addLast(LightNode.POOL.obtain().set(x, y, z+1));
			}
			if (x+1 < size)
			if (!BlocksList.get(data[x+1][y][z]).getMaterial().canBlockLights() && toSunLight(data[x+1][y][z])+2 <= lightLevel) {
				world.setSunLight(x+1, y, z, lightLevel-1);
				skylightQue.addLast(LightNode.POOL.obtain().set(x+1, y, z));
			}
			
			LightNode.POOL.free(node);
		}
	}
}
