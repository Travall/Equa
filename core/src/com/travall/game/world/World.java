package com.travall.game.world;

import static com.badlogic.gdx.math.MathUtils.floor;
import static com.travall.game.utils.BlockUtils.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Null;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.math.ChunkPlane;
import com.travall.game.world.chunk.ChunkBuilder;
import com.travall.game.world.chunk.ChunkMesh;
import com.travall.game.world.chunk.CombinedChunk;
import com.travall.game.world.gen.Generator;
import com.travall.game.world.lights.LightHandle;

public final class World implements Disposable {
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
	
	public static final int waterLevel = Math.round(mapHeight/3.4f); // 4.5f
	
	public static final LightHandle lightHandle = new LightHandle(true);

	public final int[][][] data;
	public final short[][] shadowMap;
	
	private final BlockPos blockPos = new BlockPos();
	private final ChunkMesh[][][] opaqueChunkMeshes;
	private final ChunkMesh[][][] transparentChunkMeshes;
	private final ChunkPlane[] planes = new ChunkPlane[4];
	
	public final FileHandle folder;

	public World(FileHandle folder, @Null Generator generator) {
		World.world = this;
		this.folder = folder;
		
		for (int i = 0; i < planes.length; i++) {
			planes[i] = new ChunkPlane();
		}
		
		this.data = STATIC_DATA;
		this.shadowMap = new short[mapSize][mapSize];
		opaqueChunkMeshes = new ChunkMesh[xChunks][yChunks][zChunks];
		transparentChunkMeshes = new ChunkMesh[xChunks][yChunks][zChunks];
		
		if (generator != null) generator.genrate(this);
	}
	
	public void buildMesh() {
		final int haft = chunkSize / 2;
		for (int x = 0; x < xChunks; x++)
		for (int y = 0; y < yChunks; y++)
		for (int z = 0; z < zChunks; z++) {
			CombinedChunk combinedChunk = ChunkBuilder.buildChunk(x*chunkSize, y*chunkSize, z*chunkSize, chunkSize, null,null);
			
			final float
			xPos = (x << chunkShift) + haft,
			yPos = (y << chunkShift) + haft,
			zPos = (z << chunkShift) + haft;
			
			opaqueChunkMeshes[x][y][z] = combinedChunk.opaque.setPos(xPos, yPos, zPos);
			transparentChunkMeshes[x][y][z] = combinedChunk.transparent.setPos(xPos, yPos, zPos);
		}
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

	private final Array<ChunkMesh> transMeshes = new Array<>(32);
	
	public void render(Camera camera) {
		lightHandle.calculateLights(true); // Calculate lights.
		
		final Plane[] tmpPlanes =  camera.frustum.planes;
		for (int i = 2; i < tmpPlanes.length; i++) {
			planes[i-2].set(tmpPlanes[i]);
		}

		UltimateTexture.texture.bind();
		VoxelTerrain.begin(camera);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		transMeshes.size = 0;
		for(int x = 0; x < xChunks; x++)
		for(int y = 0; y < yChunks; y++)
		for(int z = 0; z < zChunks; z++) {
			ChunkMesh mesh = opaqueChunkMeshes[x][y][z];
			if (mesh.isDirty) {
				ChunkBuilder.buildChunk(x*chunkSize, y*chunkSize, z*chunkSize, chunkSize, opaqueChunkMeshes[x][y][z],transparentChunkMeshes[x][y][z]);
			}

			int isVisable = -1;
			if(!mesh.isEmpty && (isVisable = mesh.isVisable(planes)?1:0) == 1) {
				mesh.render();
			}
			
			mesh = transparentChunkMeshes[x][y][z];
			if (!mesh.isEmpty && (isVisable == 1 || (isVisable == -1 && mesh.isVisable(planes)))) {
				transMeshes.add(mesh);
			}
		}
		Gdx.gl30.glBindVertexArray(0);
		
		if (transMeshes.notEmpty()) {
			if (getBlock(blockPos.set(floor(camera.position.x), floor(camera.position.y), floor(camera.position.z))).getMaterial().isTransparent())
				Gdx.gl.glDisable(GL20.GL_CULL_FACE);

			Gdx.gl.glEnable(GL20.GL_BLEND);
			for (ChunkMesh mesh : transMeshes) {
				mesh.render();
			}
			Gdx.gl30.glBindVertexArray(0);
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		VoxelTerrain.end();
	}

	public void setMeshDirtyShellAt(int x, int y, int z) {
		final int indexX = x >> chunkShift;
		final int indexY = y >> chunkShift;
		final int indexZ = z >> chunkShift;
		setMeshDirtyAt(indexX, indexY, indexZ);

		if ((x & chunkMask) == 0) {
			setMeshDirtyAt(indexX - 1, indexY, indexZ);
		}

		if ((x & chunkMask) == 15) {
			setMeshDirtyAt(indexX + 1, indexY, indexZ);
		}

		if ((y & chunkMask) == 0) {
			setMeshDirtyAt(indexX, indexY - 1, indexZ);
		}

		if ((y & chunkMask) == 15) {
			setMeshDirtyAt(indexX, indexY + 1, indexZ);
		}

		if ((z & chunkMask) == 0) {
			setMeshDirtyAt(indexX, indexY, indexZ - 1);
		}

		if ((z & chunkMask) == 15) {
			setMeshDirtyAt(indexX, indexY, indexZ + 1);
		}
	}

	public void setMeshDirtyAt(int indexX, int indexY, int indexZ) {
		if (indexX < 0 || indexX >= xChunks || indexY < 0 || indexY >= yChunks || indexZ < 0 || indexZ >= zChunks)
			return;

		opaqueChunkMeshes[indexX][indexY][indexZ].isDirty = true;
		transparentChunkMeshes[indexX][indexY][indexZ].isDirty = true;
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
		for (int x = 0; x < xChunks; x++)
		for (int y = 0; y < yChunks; y++)
		for (int z = 0; z < zChunks; z++) {
			opaqueChunkMeshes[x][y][z].dispose();
			transparentChunkMeshes[x][y][z].dispose();
		}
		
		final int[][][] data = this.data;
		for (int x = 0; x < mapSize; x++)
		for (int y = 0; y < mapHeight; y++)
		for (int z = 0; z < mapSize; z++) {
			data[x][y][z] = 0;
		}

		World.world = null;
	}
}
