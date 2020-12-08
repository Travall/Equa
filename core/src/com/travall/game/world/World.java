package com.travall.game.world;

import static com.travall.game.utils.BlockUtils.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.renderer.Skybox;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Properties;
import com.travall.game.utils.Serializable;
import com.travall.game.world.chunk.ChunkManager;
import com.travall.game.world.gen.Generator;
import com.travall.game.world.lights.LightHandle;

public final class World implements Disposable, Serializable {
	/** Easy world access. */
	public static World world;

	public static final int mapSize = 512;
	public static final int mapHeight = 256;

	public static final int chunkShift = 4;
	public static final int chunkSize = 1 << chunkShift;
	public static final int chunkMask = chunkSize - 1;
	public static final int xChunks = mapSize / chunkSize;
	public static final int yChunks = mapHeight / chunkSize;
	public static final int zChunks = mapSize / chunkSize;
	public static final int[][][] STATIC_DATA = new int[mapSize][mapHeight][mapSize];
	
	public static final int waterLevel = Math.round(mapHeight/2.5f); // 4.5f
	
	public static final LightHandle lightHandle = new LightHandle(true);

	public final int[][][] data;
	public final short[][] shadowMap;
	
	public final ChunkManager chunkManager;
	public final FileHandle folder;
	public Skybox skybox;
	
	public float cycle;

	public World(FileHandle folder, Generator generator) {
		World.world = this;
		this.folder = folder;
		
		this.data = STATIC_DATA;
		this.shadowMap = new short[mapSize][mapSize];
		this.chunkManager = new ChunkManager();
		
		generator.genrate(this);
	}
	
	public World(FileHandle folder) {
		World.world = this;
		this.folder = folder;
		
		this.data = STATIC_DATA;
		this.shadowMap = new short[mapSize][mapSize];
		this.chunkManager = new ChunkManager();
	}
	
	public void intsMeshes() {
		chunkManager.intsMeshes();
		skybox = new Skybox(this);
	}
	
	public void createShadowMap(final boolean fillLights) {
		for (int x = 0; x < mapSize; x++)
		for (int z = 0; z < mapSize; z++)
		for (int y = mapHeight-1; y >= 0; y--) {
			if (BlocksList.get(data[x][y][z]).getMaterial().canBlockSunRay()) {
				shadowMap[x][z] = (short)y;
				break;
			}
			if (fillLights) setSunLight(x, y, z, 15);
		}
	}

	public short getShadow(int x, int z) {
		if (x < 0 || z < 0 || x >= mapSize || z >= mapSize)
			return mapHeight;

		return shadowMap[x][z];
	}
	
	public void render(PerspectiveCamera camera) {
		lightHandle.calculateLights(true); // Calculate lights.
		
		cycle += 0.1f * Gdx.graphics.getDeltaTime();
		if (cycle > MathUtils.PI2) {
			cycle -= MathUtils.PI2;
		}
		cycle = MathUtils.HALF_PI;
		skybox.render(camera);
		
		chunkManager.render(camera);
	}

	public void setMeshDirtyShellAt(int x, int y, int z) {
		final int indexX = x >> chunkShift;
		final int indexY = y >> chunkShift;
		final int indexZ = z >> chunkShift;
		chunkManager.setDirtyIndex(indexX, indexY, indexZ);

		if ((x & chunkMask) == 0) {
			chunkManager.setDirtyIndex(indexX - 1, indexY, indexZ);
		}

		if ((x & chunkMask) == chunkMask) {
			chunkManager.setDirtyIndex(indexX + 1, indexY, indexZ);
		}

		if ((y & chunkMask) == 0) {
			chunkManager.setDirtyIndex(indexX, indexY - 1, indexZ);
		}

		if ((y & chunkMask) == chunkMask) {
			chunkManager.setDirtyIndex(indexX, indexY + 1, indexZ);
		}

		if ((z & chunkMask) == 0) {
			chunkManager.setDirtyIndex(indexX, indexY, indexZ - 1);
		}

		if ((z & chunkMask) == chunkMask) {
			chunkManager.setDirtyIndex(indexX, indexY, indexZ + 1);
		}
	}

	public boolean isAirBlock(int x, int y, int z) {
		return isOutBound(x, y, z) || toBlockID(data[x][y][z]) == 0;
	}

	public void setBlock(int x, int y, int z, Block block) {
		if (isOutBound(x, y, z)) return;

		data[x][y][z] = (data[x][y][z] & NODATA) | block.getID();
	}

	public Block getBlock(BlockPos pos) {
		return isOutBound(pos.x, pos.y, pos.z) ? BlocksList.AIR : BlocksList.get(data[pos.x][pos.y][pos.z]);
	}

	public boolean isOutBound(int x, int y, int z) {
		return x < 0 || y < 0 || z < 0 || x >= mapSize || y >= mapHeight || z >= mapSize;
	}

	public int getData(BlockPos pos) {
		return getData(pos.x, pos.y, pos.z);
	}

	public int getData(int x, int y, int z) {
		return isOutBound(x, y, z) ? 0xF0000000 : data[x][y][z];
	}

	// Set the bits XXXX0000
	public void setSunLight(int x, int y, int z, int val) {
		data[x][y][z] = (data[x][y][z] & SUN_INV) | (val << SUN_SHIFT);
	}

	// Set the bits 0000XXXX
	public void setSrcLight(int x, int y, int z, int val) {
		data[x][y][z] = (data[x][y][z] & SRC_INV) | (val << SRC_SHIFT);
	}

	@Override
	public void dispose() {
		chunkManager.dispose();
		skybox.dispose();
		
		final int[][][] data = this.data;
		for (int x = 0; x < mapSize; x++)
		for (int y = 0; y < mapHeight; y++)
		for (int z = 0; z < mapSize; z++) {
			data[x][y][z] = 0;
		}

		World.world = null;
	}

	@Override
	public void write(Properties props) {
		Properties worldProps = props.newProps("world");
		worldProps.put("cycle", cycle);
	}

	@Override
	public void read(Properties props) {
		Properties worldProps = props.getProps("world");
		cycle = worldProps.got("cycle", 1f);
	}
}
