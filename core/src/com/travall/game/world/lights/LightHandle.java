package com.travall.game.world.lights;

import static com.travall.game.world.World.*;

import com.travall.game.utils.BlockPos;

public final class LightHandle {

	public static void newSrclightAt(int x, int y, int z, int light) {
		world.setSrcLight(x, y, z, light);
		SrcLight.newSrclightAt(x, y, z);
	}
	
	public static void newSunlightAt(int x, int y, int z, int light) {
		world.setSunLight(x, y, z, light);
		SunLight.newSunlightAt(x, y, z);
	}
	
	public static void delSrclightAt(int x, int y, int z) {
		SrcLight.delSrclightAt(x, y, z);
		world.setSrcLight(x, y, z, 0);
	}
	
	public static void delSunlightAt(int x, int y, int z) {
		SunLight.delSunlightAt(x, y, z);
		world.setSunLight(x, y, z, 0);
	}
	
	public static void newSrclightShellAt(int x, int y, int z) {
		if (y+1 < mapHeight) {
			SrcLight.newSrclightAt(x, y+1, z);
		}
		if (y-1 >= 0) {
			SrcLight.newSrclightAt(x, y-1, z);
		}
		if (z-1 >= 0) {
			SrcLight.newSrclightAt(x, y, z-1);
		}
		if (x-1 >= 0) {
			SrcLight.newSrclightAt(x-1, y, z);
		}
		if (z+1 < mapSize) {
			SrcLight.newSrclightAt(x, y, z+1);
		}
		if (x+1 < mapSize) {
			SrcLight.newSrclightAt(x+1, y, z);
		}
	}
	
	public static void newSunlightShellAt(int x, int y, int z) {
		if (y+1 < mapHeight) {
			SunLight.newSunlightAt(x, y+1, z);
		}
		if (y-1 >= 0) {
			SunLight.newSunlightAt(x, y-1, z);
		}
		if (z-1 >= 0) {
			SunLight.newSunlightAt(x, y, z-1);
		}
		if (x-1 >= 0) {
			SunLight.newSunlightAt(x-1, y, z);
		}
		if (z+1 < mapSize) {
			SunLight.newSunlightAt(x, y, z+1);
		}
		if (x+1 < mapSize) {
			SunLight.newSunlightAt(x+1, y, z);
		}
	}
	
	public static void updateSunAt(int x, int y, int z) {
		
	}
	
	private static final BlockPos blockPos = new BlockPos();
	public static void skyRay(int x, int z, int height, boolean canBlockSunRay) {
		final int start = world.shadowMap[x][z];
		if (start > height) return;
		for (short y = (short)height; y >= 0; y--)
		{
			if (world.getBlock(blockPos.set(x, y, z)).getMaterial().canBlockSunRay()) {
				world.shadowMap[x][z] = y;
				break;
			}
			world.setSunLight(x, y, z, 15);
		}
		
		final int end = world.shadowMap[x][z];
		//if (start == end) return;
		
		if (start < end) {
			for(int i = start; i < end; i++) {
				delSunlightAt(x, i, z);
			}
		} else {
			for(int i = end+1; i < start; i++) {
				newSunlightAt(x, i, z, 15);
			}
		}
	}

	public static void fillSrclight() {
		SrcLight.fillSrclight();
	}
	
	public static void fillSunlight(boolean updateMesh) {
		SunLight.fillSunlight(updateMesh);
	}
	
	public static void defillSrclight() {
		SrcLight.defillSrclight();
	}

	public static void defillSunlight() {
		SunLight.defillSunlight();
	}
}
