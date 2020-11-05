package com.travall.game.world.lights;

import static com.travall.game.world.World.*;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.travall.game.utils.BlockPos;

public final class LightHandle {
	
	private final Pool<GridPoint3> pool = new Pool<GridPoint3>() {
		protected GridPoint3 newObject() {
			return new GridPoint3();
		}
	};
	
	private Array<GridPoint3> rayUpdate = new Array<>();
	
	private final SrcLight srcLight;
	private final SunLight sunLight;
	
	public LightHandle() {
		srcLight = new SrcLight();
		sunLight = new SunLight();
	}
	
	public LightHandle(Pool<LightNode> pool1, Pool<LightDelNode> pool2) {
		srcLight = new SrcLight(pool1, pool2);
		sunLight = new SunLight(pool1, pool2);
	}

	public void newSrclightAt(int x, int y, int z, int light) {
		world.setSrcLight(x, y, z, light);
		srcLight.newSrclightAt(x, y, z);
	}
	
	public void newSunlightAt(int x, int y, int z, int light) {
		world.setSunLight(x, y, z, light);
		sunLight.newSunlightAt(x, y, z);
	}
	
	public void delSrclightAt(int x, int y, int z) {
		srcLight.delSrclightAt(x, y, z);
		world.setSrcLight(x, y, z, 0);
	}
	
	public void delSunlightAt(int x, int y, int z) {
		sunLight.delSunlightAt(x, y, z);
		world.setSunLight(x, y, z, 0);
	}
	
	public void newRaySunlightAt(int x, int y, int z) {
		if (world.shadowMap[x][z] > y) return;
		rayUpdate.add(pool.obtain().set(x, y, z));
	}
	
	public void newSrclightShellAt(int x, int y, int z) {
		if (y+1 < mapHeight) {
			srcLight.newSrclightAt(x, y+1, z);
		}
		if (y-1 >= 0) {
			srcLight.newSrclightAt(x, y-1, z);
		}
		if (z-1 >= 0) {
			srcLight.newSrclightAt(x, y, z-1);
		}
		if (x-1 >= 0) {
			srcLight.newSrclightAt(x-1, y, z);
		}
		if (z+1 < mapSize) {
			srcLight.newSrclightAt(x, y, z+1);
		}
		if (x+1 < mapSize) {
			srcLight.newSrclightAt(x+1, y, z);
		}
	}
	
	public void newSunlightShellAt(int x, int y, int z) {
		sunLight.newSunlightAt(x, y, z);
		if (y+1 < mapHeight) {
			sunLight.newSunlightAt(x, y+1, z);
		}
		if (y-1 >= 0) {
			sunLight.newSunlightAt(x, y-1, z);
		}
		if (z-1 >= 0) {
			sunLight.newSunlightAt(x, y, z-1);
		}
		if (x-1 >= 0) {
			sunLight.newSunlightAt(x-1, y, z);
		}
		if (z+1 < mapSize) {
			sunLight.newSunlightAt(x, y, z+1);
		}
		if (x+1 < mapSize) {
			sunLight.newSunlightAt(x+1, y, z);
		}
	}
	
	public void updateSunAt(int x, int y, int z) {
		
	}
	
	private final BlockPos blockPos = new BlockPos();
	public void skyRay(int x, int z, int height) {
		final int start = world.shadowMap[x][z];
		for (short y = (short)height; y >= 0; y--)
		{
			if (world.getBlock(blockPos.set(x, y, z)).getMaterial().canBlockSunRay()) {
				world.shadowMap[x][z] = y;
				break;
			}
			world.setSunLight(x, y, z, 15);
		}
		
		final int end = world.shadowMap[x][z];
		if (start == end) return;
		
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
	
	public void calculateLights(final boolean updateMesh) {
		for (GridPoint3 pos : rayUpdate) {
			skyRay(pos.x, pos.z, pos.y);
			pool.free(pos);
		}
		rayUpdate.size = 0;
		
		srcLight.defillSrclight();
		srcLight.fillSrclight();
		sunLight.defillSunlight(updateMesh);
		sunLight.fillSunlight(updateMesh);
	}
}
