package com.travall.game.world.gen;

import static com.travall.game.world.World.*;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.blocks.materials.Material;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Utils;
import com.travall.game.utils.math.FastNoiseOctaves;
import com.travall.game.utils.math.OpenSimplexOctaves;
import com.travall.game.world.World;
import com.travall.game.world.biomes.Biome;
import com.travall.game.world.biomes.Carmine;
import com.travall.game.world.biomes.Desert;
import com.travall.game.world.biomes.Ground;
import com.travall.game.world.lights.LightHandle;

public class DefaultGen extends Generator {

	private final long seed;
	
	private final BlockPos blockPos = new BlockPos();
	private final Vector3 tempVec3 = new Vector3();
	private final Vector2 tempVec2 = new Vector2();
	private final Biome[] biomes = {new Desert(), new Ground(), new Carmine()};
	
	public DefaultGen() {
		this(new Random().nextLong());
	}
	
	public DefaultGen(long seed) {
		this.seed = seed;
	}
	
	@Override
	public void genrate(final World world) {
		setStatus("Creating noises..");
		final Random random = new Random(seed);
		final FastNoiseOctaves CaveNoise = new FastNoiseOctaves(5, 0.25, random.nextLong());
		final OpenSimplexOctaves FloatingIslandNoise = new OpenSimplexOctaves(7, 0.35, random.nextLong());
		int maxTerrainHeight = Math.round(mapHeight / 1.7f);
		final int terrainHeightOffset = 20;
		final Biome prevalentBiomes[][] = new Biome[mapSize][mapSize];

		for(int i = 0; i < biomes.length; i++) {
			biomes[i].heightMap = new OpenSimplexOctaves(biomes[i].heightOctaves, biomes[i].heightPersistence, random.nextLong());
			biomes[i].decisionMap = new OpenSimplexOctaves(biomes[i].decisionOctaves, biomes[i].decisionPersistence, random.nextLong());
		}

		setStatus("Creating Heightmap..");
		final float heights[][]  = new float[mapSize][mapSize];
		for (int x = 0; x < mapSize; x++)
		for (int z = 0; z < mapSize; z++) {
			Biome prevalent = getPrevalent(x/1.5,z/1.5);
			prevalentBiomes[x][z] = prevalent;
			heights[x][z] = terrainHeightOffset + (float)(Utils.normalize(prevalent.heightMap.getNoise(x, z), maxTerrainHeight)  * prevalent.heightModifier);
		}
		maxTerrainHeight += terrainHeightOffset;
		
		setStatus("Creating Lands..");
		for (int x = 0; x < mapSize; x++)
		for (int z = 0; z < mapSize; z++) {
			final Biome primary = prevalentBiomes[x][z];

			final float height = gaussian(heights, x, z);
			final int yValue = (int) height;
			
			boolean middleDone = false;
			
			for (int i = yValue; i >= 0; i--) {
				
				middleDone = middleDone ? true : i == yValue - (Math.abs(i - maxTerrainHeight) / 4);
				
				if (i == 0) {
					world.setBlock(x, i, z, BlocksList.BEDROCK);
				} else if (i == yValue) {
					if (i < waterLevel) {
						world.setBlock(x, i, z, primary.underwater);
					} else {
						world.setBlock(x, i, z, primary.top);
					}
				} else if (!middleDone) {
					if (i < waterLevel) {
						world.setBlock(x, i, z, primary.underwater);
					} else {
						world.setBlock(x, i, z, primary.middle);
					}
				} else {
					world.setBlock(x, i, z, BlocksList.STONE);
				}
			}
			
			for (int y = waterLevel; y >= 0; y--) {
				if (world.isAirBlock(x, y, z)) {
					world.setBlock(x, y, z, BlocksList.WATER);
					continue;
				}
				
				break;
			}
			
			for (int i = yValue; i >= 0; i--) {
				final float caves = Utils.normalize(CaveNoise.getNoise(x, i, z), maxTerrainHeight) * 0.95f;
				if (caves >= maxTerrainHeight - (height - i / 2f) && caves > maxTerrainHeight / 2f && i > 0) {
					world.setBlock(x, i, z, BlocksList.AIR);
				}
			}
		}

		setStatus("Building SkyIslands..");
		for (int x = 0; x < mapSize; x++)
		for (int z = 0; z < mapSize; z++) {
			int centerX = mapSize / 4;
			int centerY = mapHeight - mapHeight / 4;
			int centerZ = mapSize / 4;

			if(x >= mapSize / 2) {
				centerX = mapSize - centerX;
			}

			if(z >= mapSize / 2) {
				centerZ = mapSize - centerZ;
			}

			if(Math.abs(tempVec2.set(x,z).dst(mapSize / 2,mapSize / 2)) < mapSize / 6) {
				centerX = mapSize / 2;
				centerZ = mapSize / 2;
			}

			for(int y = mapHeight; y > mapHeight / 2; y--) {
				double diff = dst(centerX,centerY,centerZ,x,y,z) / 50.0;

				if(FloatingIslandNoise.getNoise(x,y,z) / diff > 0.18) {
					if(world.isAirBlock(x,y,z)) {
						if(world.isAirBlock(x,y+1,z)) {
							world.setBlock(x,y,z,BlocksList.GRASS);
							if(random.nextInt(10) == 1) {
								world.setBlock(x,y+1,z,BlocksList.TALLGRASS);
							}
						} else if(world.getBlock(blockPos.set(x,y+1,z)) == BlocksList.GRASS) {
							world.setBlock(x,y,z,BlocksList.DIRT);
						} else {
							world.setBlock(x,y,z,BlocksList.STONE);
						}
					}
				}
			}
		}

		setStatus("Creating Foliages..");
		for (int x = 0; x < mapSize; x++)
		for (int z = 0; z < mapSize; z++) {
			Biome primary = prevalentBiomes[x][z];
			// Foliage
			for(int j = mapHeight; j > 0; j--) {
				if(!world.isAirBlock(x,j,z)) {
					if(world.getBlock(blockPos.set(x,j,z)) == BlocksList.GRASS || world.getBlock(blockPos.set(x,j,z)) == BlocksList.CARMINE) {

						if(random.nextInt(10) == 1) {
							if(world.getBlock(blockPos.set(x,j,z)) == BlocksList.GRASS) world.setBlock(x,j+1,z,BlocksList.TALLGRASS);
							if(world.getBlock(blockPos.set(x,j,z)) == BlocksList.CARMINE) world.setBlock(x,j+1,z,BlocksList.DARKSHRUB);
						}

						boolean open = world.isAirBlock(x,j + 1,z) && world.isAirBlock(x,j + 2,z) && world.isAirBlock(x,j + 3,z);

						if(!open) continue;

						for (int xx = x - 2; xx <= x + 2; xx++) {
							for (int zz = z - 2; zz <= z + 2; zz++) {
								if(!world.isAirBlock(xx, j+4,zz)) {
									open = false;
									break;
								}
							}
						}

						if(!open) continue;

						for (int xx = x - 1; xx <= x + 1; xx++) {
							for (int zz = z - 1; zz <= z + 1; zz++) {
								if(!world.isAirBlock(xx, j+5,zz)) {
									open = false;
									break;
								}
							}
						}

						if(!open) continue;

						//Block log = BlocksList.LOG;

						Block logType = primary.name.equals("Ground") ? BlocksList.LOG : BlocksList.DARKLOG;
						Block leavesType = primary.name.equals("Ground") ? BlocksList.LEAVES : BlocksList.DARKLEAVES;

						if (random.nextInt(100) == 1 && x > 0 && z > 0 && x <= mapSize && z <= mapSize) {
							world.setBlock(x, j+1, z, logType);
							world.setBlock(x, j+2, z, logType);
							world.setBlock(x, j+3, z, logType);

							for (int xx = x - 2; xx <= x + 2; xx++) {
								for (int zz = z - 2; zz <= z + 2; zz++) {
									world.setBlock(xx, j+4, zz, leavesType);
								}
							}

							for (int xx = x - 1; xx <= x + 1; xx++) {
								for (int zz = z - 1; zz <= z + 1; zz++) {
									world.setBlock(xx, j+5, zz, leavesType);
								}
							}

							world.setBlock(x, j+4, z, logType);
							world.setBlock(x, j+6, z, leavesType);

						}
					} else if (world.getBlock(blockPos.set(x,j,z)) == BlocksList.SAND && world.isAirBlock(x,j+1,z)) {
						if(random.nextInt(100) == 1) {
							world.setBlock(x,j+1,z,BlocksList.SHRUB);
						}
						if (random.nextInt(200) == 1 && j > waterLevel) {
							world.setBlock(x, j+1, z, BlocksList.CACTUS);
							world.setBlock(x, j+2, z, BlocksList.CACTUS);
							world.setBlock(x, j+3, z, BlocksList.CACTUS);
						}
					}
				}
			}
		}

		// creating shadow map.
		setStatus("Creating Shadow Map..");
		world.createShadowMap(true);
		
		// adding filling nodes.
		setStatus("Filling Skylights..");
		final LightHandle lightHandle = new LightHandle(false);
		for (int x = 0; x < mapSize; x++)
		for (int z = 0; z < mapSize; z++)
		for (int y = world.shadowMap[x][z]; y >= 0; y--) {
			final Material material = BlocksList.get(world.data[x][y][z]).getMaterial();
			
			if (material.canBlockSunRay() && material.canBlockLights()) {
				continue;
			}
			
			if (world.getShadow(x+1, z) < y || world.getShadow(x, z+1) < y ||
				world.getShadow(x-1, z) < y || world.getShadow(x, z-1) < y || world.getShadow(x, z) < y+1) {
	
				lightHandle.newSunlightAt(x, y, z, 14);
			}
			
			continue;
		}
		lightHandle.calculateLights(false);
		
		setStatus("Done!");
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
	}

	private Biome getPrevalent(double x, double z) {
		Biome prevalent = biomes[0];
		for(int i = 0; i < biomes.length; i++) {
			if(Utils.normalize(biomes[i].decisionMap.getNoise(x, z)+biomes[i].size, 1) > Utils.normalize(prevalent.decisionMap.getNoise(x, z)+prevalent.size, 1)) {
				prevalent = biomes[i];
			}
		}
		return prevalent;
	}
	
	// Gaussian matrix.	
	private static final int GAUSSIAN_SIZE = 15;
	private static final float[][] GAUSSIAN_MATRIX = new float[GAUSSIAN_SIZE][GAUSSIAN_SIZE];
	static {
		final int haft = GAUSSIAN_SIZE / 2;
		final float size = GAUSSIAN_SIZE / 2.0f;
		for (int x = 0; x < GAUSSIAN_SIZE; x++)
		for (int z = 0; z < GAUSSIAN_SIZE; z++) {
			final int xx = x - haft;
			final int zz = z - haft;
			final float sample = 1.0f - (sqrt((xx*xx)+(zz*zz)) / size);
			GAUSSIAN_MATRIX[x][z] = sample > 0.0f ? sample : 0.0f;
		}
		
	}

	/** Bilinear interpolation. */
	private static float gaussian(float[][] map, int x, int z) {
		float height = 0;
		float total = 0;

		x -= GAUSSIAN_SIZE / 2;
		z -= GAUSSIAN_SIZE / 2;
		for(int i = x; i < x + GAUSSIAN_SIZE; i++) {
			for(int j = z; j < z + GAUSSIAN_SIZE; j++) {
				if(Utils.inBounds(i,map.length) && Utils.inBounds(j,map[0].length)) {
					float sample = GAUSSIAN_MATRIX[i-x][j-z];
					height += map[i][j] * sample;
					total += sample;
				}
			}
		}

		return height / total;
	}
	
	private static float sqrt(int a) {
		return (float)Math.sqrt(a);
	}
	
	private static double dst(int x0, int y0, int z0, int x1, int y1, int z1) {
		final int a = x1 - x0;
		final int b = y1 - y0;
		final int c = z1 - z0;
		return Math.sqrt(a * a + b * b + c * c);
	}
}
