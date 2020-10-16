package com.travall.game.world;

import static com.badlogic.gdx.math.MathUtils.floor;
import static com.travall.game.utils.BlockUtils.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Utils;
import com.travall.game.utils.math.OpenSimplexOctaves;
import com.travall.game.world.chunk.ChunkBuilder;
import com.travall.game.world.chunk.ChunkMesh;
import com.travall.game.world.chunk.CombinedChunk;
import com.travall.game.world.lights.LightHandle;

public final class World implements Disposable {
	/** Easy world access. */
	public static World world;

	public static final int mapSize = 512;
	public static final int mapHeight = 256;

	public static final int chunkShift = 4; // 1 << 4 = 16. I set it back from 32 to 16 due to vertices limitations.
	public static final int chunkSize = 1 << chunkShift;
	public static final int chunkMask = chunkSize - 1;
	public static final int xChunks = mapSize / chunkSize;
	public static final int yChunks = mapHeight / chunkSize;
	public static final int zChunks = mapSize / chunkSize;

	public static final int waterLevel = Math.round(mapHeight/4.5f); // 4.5f

	final public int[][][] data;
	final public short[][] shadowMap;
	final ChunkBuilder blockBuilder;
	final GridPoint3 pos = new GridPoint3();
	final BlockPos tmpBlockPos = new BlockPos();

	final ChunkMesh[][][] opaqueChunkMeshes;
	final ChunkMesh[][][] transparentChunkMeshes;

	public World() {
		World.world = this;

		this.data = new int[mapSize][mapHeight][mapSize];
		this.shadowMap = new short[mapSize][mapSize];
		this.blockBuilder = new ChunkBuilder(this);
		generate(MathUtils.random.nextLong());

		opaqueChunkMeshes = new ChunkMesh[xChunks][yChunks][zChunks];
		transparentChunkMeshes = new ChunkMesh[xChunks][yChunks][zChunks];
		for (int x = 0; x < xChunks; x++)
		for (int y = 0; y < yChunks; y++)
		for (int z = 0; z < zChunks; z++) {
			CombinedChunk combinedChunk = (blockBuilder.buildChunk(x*chunkSize, y*chunkSize, z*chunkSize, chunkSize, null,null));
			opaqueChunkMeshes[x][y][z] = combinedChunk.opaque;
			transparentChunkMeshes[x][y][z] = combinedChunk.transparent;
		}
	}

	private void generate(long seed) {
		final RandomXS128 random = new RandomXS128(seed);
		OpenSimplexOctaves BaseNoise = new OpenSimplexOctaves(8, 0.4, random.nextLong());
		OpenSimplexOctaves CaveNoise = new OpenSimplexOctaves(5, 0.25, random.nextLong());
		OpenSimplexOctaves FloatingIslandNoise = new OpenSimplexOctaves(7, 0.35, random.nextLong());
		OpenSimplexOctaves DecisionNoise = new OpenSimplexOctaves(8, 0.4, random.nextLong());
		OpenSimplexOctaves Decision2Noise = new OpenSimplexOctaves(8, 0.4, random.nextLong());

		int maxTerrainHeight = Math.round(mapHeight / 1.8f);

		for (int x = 0; x < mapSize; x++) {
			for (int z = 0; z < mapSize; z++) {
				double base = Utils.normalize(BaseNoise.getNoise(x, z), maxTerrainHeight);
				double mountain = Utils.normalize(BaseNoise.getNoise(x, z), maxTerrainHeight) * 2;
				double decision = Utils.normalize(DecisionNoise.getNoise(x, z), 1);
				double decision2 = Utils.normalize(Decision2Noise.getNoise(x, z), 1);

				float steep = Interpolation.exp5.apply(Interpolation.exp5.apply((Interpolation.exp5.apply((float) decision))));
				float steep2 = Interpolation.exp5.apply(Interpolation.exp5.apply((Interpolation.exp10.apply((float) decision2))));

				float height = MathUtils.lerp((float) base, (float) mountain, steep);

				int yValue = (Math.round(height / 1) * 1);

				boolean sandDone = false;

				for (int i = yValue; i >= 0; i--) {
					double caves = Utils.normalize(CaveNoise.getNoise(x, (i), z), maxTerrainHeight);
					boolean caveTerritory = (caves >= maxTerrainHeight - (height - i) && caves > maxTerrainHeight / 2
							&& i > 0);

 					if((getBlock(tmpBlockPos.set(x,i+1,z)) == BlocksList.SAND && getBlock(tmpBlockPos.set(x,i+2,z)) == BlocksList.SAND && getBlock(tmpBlockPos.set(x,i+3,z)) == BlocksList.SAND && getBlock(tmpBlockPos.set(x,i+4,z)) == BlocksList.SAND)) sandDone = true;

					if (i == 0) {
						setBlock(x, i, z, BlocksList.BEDROCK);
					} else {
						if (i == yValue && i >= waterLevel) {
							if (steep < 0.3 && steep2 < 0.5) {
								setBlock(x, i, z, BlocksList.SAND);
								if(random.nextInt(100) == 1) {
									setBlock(x,i+1,z,BlocksList.SHRUB);
								}
							} else {
								setBlock(x, i, z, BlocksList.GRASS);
								if(random.nextInt(10) == 1) {
									setBlock(x,i+1,z,BlocksList.TALLGRASS);
								}
							}
						} else if (!caveTerritory) {
							if (steep < 0.3 && steep2 < 0.5 && !sandDone) {
								setBlock(x, i, z, BlocksList.SAND);
							} else if (caves >= maxTerrainHeight - (height - i) * 14) {
								setBlock(x, i, z, BlocksList.STONE);
							} else {
								setBlock(x, i, z, BlocksList.DIRT);
							}

						}
					}
				}

				// Water
				for (int j = waterLevel; j > 0; j--) {
					double caves = Utils.normalize(CaveNoise.getNoise(x, (int) (j), z), maxTerrainHeight);
					boolean caveTerritory = (caves >= maxTerrainHeight - (height - j) && caves > maxTerrainHeight / 2 && j > 0);
					if (isAirBlock(x, j, z) && !caveTerritory) {
						setBlock(x, j, z, BlocksList.WATER);
					} else {
						break;
					}
				}

				// Floating Islands
				for(int j = mapHeight-7; j > maxTerrainHeight * 1.5f; j--) {
					if(FloatingIslandNoise.getNoise(x,j,z) > 0.175) {
						if(isAirBlock(x,j,z)) {
							if(isAirBlock(x,j+1,z)) {
								setBlock(x,j,z,BlocksList.GRASS);
								if(random.nextInt(10) == 1) {
									setBlock(x,j+1,z,BlocksList.TALLGRASS);
								}
							} else if(getBlock(new BlockPos(x,j+1,z)) == BlocksList.GRASS) {
								setBlock(x,j,z,BlocksList.DIRT);
							} else {
								setBlock(x,j,z,BlocksList.STONE);
							}
						}
					}
				}
			}
		}
		
		// creating shadow map.
		for (int x = 0; x < mapSize; x++)
		for (int z = 0; z < mapSize; z++) {
			for (int y = mapHeight-1; y >= 0; y--) {
				if (BlocksList.get(data[x][y][z]).getMaterial().canBlockSunRay()) {
					shadowMap[x][z] = (short)y;
					break;
				}
				setSunLight(x, y, z, 15);
			}
		}
		
		// adding filling nodes.
		for (int x = 0; x < mapSize; x++)
		for (int z = 0; z < mapSize; z++) {
			for (int y = shadowMap[x][z]; y >= 0; y--) {
				final Material material = BlocksList.get(data[x][y][z]).getMaterial();
				if (material.canBlockSunRay() && material.canBlockLights()) {
					continue;
				}
				if (getShadow(x+1, z) < y || getShadow(x, z+1) < y ||
					getShadow(x-1, z) < y || getShadow(x, z-1) < y || getShadow(x, z) < y+1) {
					
					LightHandle.newSunlightAt(x, y, z, 14);
				}
				continue;
			}

			// Foliage
			for(int j = mapHeight; j > 0; j--) {
				if(!isAirBlock(x,j,z)) {
					if(getBlock(new BlockPos(x,j,z)) == BlocksList.GRASS) {

						boolean open = isAirBlock(x,j + 1,z) && isAirBlock(x,j + 2,z) && isAirBlock(x,j + 3,z);

						if(!open) continue;

						for (int xx = x - 2; xx <= x + 2; xx++) {
							for (int zz = z - 2; zz <= z + 2; zz++) {
								if(!isAirBlock(xx, j+4,zz)) {
									open = false;
									break;
								}
							}
						}

						if(!open) continue;

						for (int xx = x - 1; xx <= x + 1; xx++) {
							for (int zz = z - 1; zz <= z + 1; zz++) {
								if(!isAirBlock(xx, j+5,zz)) {
									open = false;
									break;
								}
							}
						}

						if(!open) continue;

						if (random.nextInt(100) == 1 && x > 0 && z > 0 && x <= mapSize && z <= mapSize) {
							setBlock(x, j+1, z, BlocksList.LOG);
							setBlock(x, j+2, z, BlocksList.LOG);
							setBlock(x, j+3, z, BlocksList.LOG);

							for (int xx = x - 2; xx <= x + 2; xx++) {
								for (int zz = z - 2; zz <= z + 2; zz++) {
									setBlock(xx, j+4, zz, BlocksList.LEAVES);
								}
							}

							for (int xx = x - 1; xx <= x + 1; xx++) {
								for (int zz = z - 1; zz <= z + 1; zz++) {
									setBlock(xx, j+5, zz, BlocksList.LEAVES);
								}
							}

							setBlock(x, j+4, z, BlocksList.LOG);
							setBlock(x, j+6, z, BlocksList.LEAVES);

						}
					} else if (getBlock(new BlockPos(x,j,z)) == BlocksList.SAND && isAirBlock(x,j+1,z)) {
						if (random.nextInt(200) == 1 && j > waterLevel) {
							setBlock(x, j+1, z, BlocksList.CACTUS);
							setBlock(x, j+2, z, BlocksList.CACTUS);
							setBlock(x, j+3, z, BlocksList.CACTUS);
						}
					}
				}
			}
				
		}
		
		LightHandle.fillSunlight(false);


	}
	
	public short getShadow(int x, int z) {
		if (x < 0 || z < 0 || x >= mapSize || z >= mapSize)
			return mapHeight;
		
		return shadowMap[x][z];
	}

	private final BlockPos blockPos = new BlockPos();
	public void render(Camera camera) {
		
		UltimateTexture.texture.bind();
        VoxelTerrain.begin(camera);
        Gdx.gl.glCullFace(GL20.GL_BACK);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_BLEND);
        for(int x = 0; x < xChunks; x++)
        for(int y = 0; y < yChunks; y++)
        for(int z = 0; z < zChunks; z++) {
			ChunkMesh mesh = opaqueChunkMeshes[x][y][z];
            if (mesh == null) continue;
            if (mesh.isDirty) {
            	blockBuilder.buildChunk(x*chunkSize, y*chunkSize, z*chunkSize, chunkSize, opaqueChunkMeshes[x][y][z],transparentChunkMeshes[x][y][z]);
            }

            if(camera.frustum.boundsInFrustum(x*chunkSize, y*chunkSize, z*chunkSize, chunkSize,chunkSize,chunkSize)) {
            	mesh.render();
            }
        }
        Gdx.gl30.glBindVertexArray(0);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        
        if (getBlock(blockPos.set(floor(camera.position.x), floor(camera.position.y), floor(camera.position.z))).getMaterial().isTransparent()) 
        	Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        
        for(int x = 0; x < xChunks; x++)
        for(int y = 0; y < yChunks; y++)
        for(int z = 0; z < zChunks; z++) {
			ChunkMesh mesh = transparentChunkMeshes[x][y][z];
            if (mesh == null) continue;
            if (mesh.isDirty) {
            	blockBuilder.buildChunk(x*chunkSize, y*chunkSize, z*chunkSize, chunkSize, opaqueChunkMeshes[x][y][z],transparentChunkMeshes[x][y][z]);
            }

            if(camera.frustum.boundsInFrustum(x*chunkSize, y*chunkSize, z*chunkSize, chunkSize,chunkSize,chunkSize)) {
            	mesh.render();
            }
        }
        Gdx.gl30.glBindVertexArray(0);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        
        VoxelTerrain.end();
	}

	public void breakBlock(BlockPos pos) {
		final int x = pos.x, y = pos.y, z = pos.z;
		Block block = BlocksList.get(data[x][y][z]);
		setBlock(x, y, z, BlocksList.AIR);

		if (block.isSrclight()) { // if break srclight block.
			LightHandle.delSrclightAt(x, y, z);
			LightHandle.defillSrclight();
			LightHandle.fillSrclight();
		} else { // if break non-srclight block.
			LightHandle.newSrclightShellAt(x, y, z);
			LightHandle.fillSrclight();
		}
		
		LightHandle.skyRay(x, z, y, block.getMaterial().canBlockSunRay());
		LightHandle.newSunlightAt(x, y, z, toSunLight(data[x][y][z]));
		LightHandle.newSunlightShellAt(x, y, z);
		LightHandle.defillSunlight();
		LightHandle.fillSunlight(true);
		setMeshDirtyShellAt(x, y, z);
	}

	public void placeBlock(BlockPos pos, Block block) {
		if (block.isAir()) {
			breakBlock(pos);
			return;
		}
		final int x = pos.x, y = pos.y, z = pos.z;
		setBlock(x, y, z, block);

		if (block.isSrclight()) { // if place srclight block.
			LightHandle.newSrclightAt(x, y, z, block.getLightLevel());
			LightHandle.fillSrclight();
		} else { // if place non-srclight block.
			LightHandle.delSrclightAt(x, y, z);
			LightHandle.defillSrclight();
			LightHandle.fillSrclight();
		}
		
		
		if (block.getMaterial().canBlockLights() || block.getMaterial().canBlockSunRay()) {
			LightHandle.delSunlightAt(x, y, z);
			LightHandle.skyRay(x, z, y, block.getMaterial().canBlockSunRay());
			LightHandle.defillSunlight();
			LightHandle.fillSunlight(true);
		}
		
		setMeshDirtyShellAt(x, y, z);
	}

	public void setMeshDirtyShellAt(int x, int y, int z) {
		final int indexX = x >> chunkShift;
		final int indexY = y >> chunkShift;
		final int indexZ = z >> chunkShift;
		setMeshDirtyAt(indexX, indexY, indexZ);

		if ((x & chunkMask) == 0 && x > 0) {
			setMeshDirtyAt(indexX - 1, indexY, indexZ);
		}

		if (((x + 1) & chunkMask) == 0 && x < mapSize - 1) {
			setMeshDirtyAt(indexX + 1, indexY, indexZ);
		}

		if ((y & chunkMask) == 0 && y > 0) {
			setMeshDirtyAt(indexX, indexY - 1, indexZ);
		}

		if (((y + 1) & chunkMask) == 0 && y < mapHeight - 1) {
			setMeshDirtyAt(indexX, indexY + 1, indexZ);
		}

		if ((z & chunkMask) == 0 && z > 0) {
			setMeshDirtyAt(indexX, indexY, indexZ - 1);
		}

		if (((z + 1) & chunkMask) == 0 && z < mapSize - 1) {
			setMeshDirtyAt(indexX, indexY, indexZ + 1);
		}
	}

	public void setMeshDirtyAt(int indexX, int indexY, int indexZ) {
		if (indexX < 0 || indexX >= xChunks || indexY < 0 || indexY >= yChunks || indexZ < 0 || indexZ >= zChunks)
			return;

		if (opaqueChunkMeshes[indexX][indexY][indexZ] != null)
			opaqueChunkMeshes[indexX][indexY][indexZ].isDirty = true;

		if (transparentChunkMeshes[indexX][indexY][indexZ] != null)
			transparentChunkMeshes[indexX][indexY][indexZ].isDirty = true;
	}

	public boolean isAirBlock(int x, int y, int z) {
		return isOutBound(x, y, z) || toBlockID(data[x][y][z]) == 0;
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		if (isOutBound(x, y, z)) return;
				
		data[x][y][z] = (data[x][y][z] & ID_INV) | block.getID();
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
		
		World.world = null;
	}
}
