package com.travall.game.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.Main;
import com.travall.game.blocks.*;
import com.travall.game.tools.BlockBuilder;
import com.travall.game.tools.ChunkMesh;
import com.travall.game.tools.FloodLight;
import com.travall.game.tools.LightUtil;
import com.travall.game.tools.OpenSimplexOctaves;
import com.travall.game.tools.UltimateTexture;
import com.travall.game.tools.Utils;


public class World implements Disposable {
    public final int mapWidth;
    public final int mapLength;
    public final int mapHeight;
    int waterLevel;
    public short[][][] blocks;
    private byte[][][] lights;
    BlockBuilder blockBuilder;
    FloodLight floodLight;
    public UltimateTexture ultimate;
	GridPoint3 pos = new GridPoint3();

    public World(Main main, int mapWidth, int mapHeight, int mapLength, int waterLevel) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapLength = mapLength;
        
        blocks = new short[mapWidth][mapHeight][mapLength];
        lights = new byte[mapWidth][mapHeight][mapLength];
        
        this.waterLevel = waterLevel;
        this.blockBuilder = new BlockBuilder(this, lights);
        this.floodLight = new FloodLight(this, main);
        this.ultimate = new UltimateTexture(new Texture("Tiles/ultimate3.png"));
        BlocksList.ints(ultimate);
        generate(MathUtils.random.nextLong());
    }

    private void generate(long seed) {
    	final RandomXS128 random = new RandomXS128(seed);
        OpenSimplexOctaves BaseNoise = new OpenSimplexOctaves(8,0.5, random.nextLong()); // changed from 0.45 to 0.43
        OpenSimplexOctaves CaveNoise = new OpenSimplexOctaves(5,0.25, random.nextLong());
        OpenSimplexOctaves DecisionNoise = new OpenSimplexOctaves(8,0.4, random.nextLong());
        OpenSimplexOctaves Decision2Noise = new OpenSimplexOctaves(8,0.4, random.nextLong());

<<<<<<< HEAD:core/src/com/travall/game/world/World.java
        blocks = new short[mapWidth][mapHeight][mapLength];
        lights = new byte[mapWidth][mapHeight][mapLength];

        int maxTerrainHeight = Math.round(mapHeight / 1.75f);
=======
        int maxTerrainHeight = mapHeight / 2;
>>>>>>> 3351400c47121fb6eea25ab725f7f03992b11086:core/src/com/travall/game/generation/MapGenerator.java

        for(int x = 0; x < mapWidth; x++) {
            for(int z = 0; z < mapLength; z++) {
                double base = Utils.normalize(BaseNoise.getNoise(x,z),maxTerrainHeight);
                double mountain = Utils.normalize(BaseNoise.getNoise(x,z),maxTerrainHeight) * 2;
                double decision = Utils.normalize(DecisionNoise.getNoise(x,z),1);
                double decision2 = Utils.normalize(Decision2Noise.getNoise(x,z),1);

                float steep = Interpolation.exp10.apply(Interpolation.exp5.apply((Interpolation.exp10.apply((float) decision))));
                float steep2 = Interpolation.exp10.apply(Interpolation.exp5.apply((Interpolation.exp10.apply((float) decision2))));

                float height = MathUtils.lerp((float)base,(float)mountain,steep);

                int yValue = (Math.round(height / 1) * 1);

                if(steep2 > 0.5 && yValue > waterLevel) {
                    if(random.nextInt(100) >= 99 && x > 0 && z > 0 && x < mapWidth-1 && z < mapLength-1) {
                        blocks[x][yValue + 1][z] = Log.id;
                        blocks[x][yValue + 2][z] = Log.id;
                        blocks[x][yValue + 3][z] = Log.id;


                        for(int xx = x-2; xx <= x+2; xx++) {
                            for(int zz = z-2; zz <= z+2; zz++) {
                                if(x > 1 && z > 1 && x < mapWidth-2 && z < mapLength-2) blocks[xx][yValue + 4][zz] = Leaves.id;
                            }
                        }

                        for(int xx = x-1; xx <= x+1; xx++) {
                            for(int zz = z-1; zz <= z+1; zz++) {
                                if(x > 0 && z > 0 && x < mapWidth-1 && z < mapLength-1) blocks[xx][yValue + 5][zz] = Leaves.id;
                            }
                        }


                        blocks[x][yValue + 4][z] = Log.id;
                        blocks[x][yValue + 6][z] = Leaves.id;


                    }
                } else if(steep < 0.3 && steep2 < 0.5) {
                    if(random.nextInt(100) >= 99 && x > 0 && z > 0 && x < mapWidth && z < mapLength && yValue > waterLevel) {
                        blocks[x][yValue + 1][z] = Cactus.id;
                        blocks[x][yValue + 2][z] = Cactus.id;
                        blocks[x][yValue + 3][z] = Cactus.id;
                    }
                }

                for(int i = yValue; i >= 0; i--) {
                    double caves = Utils.normalize(CaveNoise.getNoise(x, (int) (i), z), maxTerrainHeight);
                    boolean caveTerritory = (caves >= maxTerrainHeight - (height - i) && caves > maxTerrainHeight /2 && i > 0);
                    if(i == 0) {
                        blocks[x][i][z] = Bedrock.id;
                    } else {
                        if (i == yValue && i >= waterLevel) {
                            if(steep < 0.3 && steep2 < 0.5) {
                                blocks[x][i][z] = Sand.id;
                            } else {
                                blocks[x][i][z] = Grass.id;
                            }
                        } else if(!caveTerritory) {
                            if (steep < 0.3 && steep2 < 0.5) {
                                blocks[x][i][z] = Sand.id;
                            } else if(caves >= maxTerrainHeight - (height - i) * 10) {
                                blocks[x][i][z] = Stone.id;
                            } else {
                                blocks[x][i][z] = Dirt.id;
                            }

                        }
                    }
                }

                for(int j = waterLevel; j > 0; j--) {
                    double caves = Utils.normalize(CaveNoise.getNoise(x, (int) (j), z), maxTerrainHeight);
                    boolean caveTerritory = (caves >= maxTerrainHeight - (height - j) && caves > maxTerrainHeight /2 && j > 0);
                    if (blocks[x][j][z] == Air.id && !caveTerritory) {
                        blocks[x][j][z] = Water.id;
                    } else {
                        break;
                    }
                }
            }
        }



    }

    public void breakBlock(int x, int y, int z) {
    	Block block = BlocksList.get(blocks[x][y][z]);
    	blocks[x][y][z] = 0;

    	if (block.isSrclight()) { // if break srclight block.
    		floodLight.delSrclightAt(x, y, z, getSrcLight(x, y, z));
    		setSrcLight(x, y, z, 0);
    		floodLight.defillSrclight();
    		floodLight.fillSrclight();
    	} else { // if break non-srclight block.
    		if (y+1 < mapHeight) floodLight.newSrclightAt(x, y+1, z);
    		if (y-1 >= 0) floodLight.newSrclightAt(x, y-1, z);
    		if (z-1 >= 0) floodLight.newSrclightAt(x, y, z-1);
    		if (x-1 >= 0) floodLight.newSrclightAt(x-1, y, z);
    		if (z+1 < mapLength) floodLight.newSrclightAt(x, y, z+1);
    		if (x+1 < mapWidth) floodLight.newSrclightAt(x+1, y, z);
    		floodLight.fillSrclight();
    	}
    }

    public void placeBlock(int x, int y, int z, short id) {
    	Block block = BlocksList.get(id);
    	blocks[x][y][z] = id;

    	if (block.isSrclight()) { // if place srclight block.
    		setSrcLight(x, y, z, block.srclight);
    		floodLight.newSrclightAt(x, y, z);
    		floodLight.fillSrclight();
    	} else { // if place non-srclight block.
    		floodLight.delSrclightAt(x, y, z, getSrcLight(x, y, z));
    		setSrcLight(x, y, z, 0);
    		floodLight.defillSrclight();
    		floodLight.fillSrclight();
    	}
    }


    public ChunkMesh generateShell(int indexX, int indexY, int indexZ,int chunkSizeX, int chunkSizeY, int chunkSizeZ, ChunkMesh chunkMesh) {
    	blockBuilder.begin();
        Block block;
        for(int x = indexX; x < indexX + chunkSizeX; x++) {
            for(int y = indexY; y < indexY + chunkSizeY; y++) {
                for(int z = indexZ; z < indexZ + chunkSizeZ; z++) {
                    if(blockExists(x,y,z)) {
                        pos.set(x,y,z);
                        block = BlocksList.get(blocks[x][y][z]);

                        if  (!blockExists(x + 1, y, z) || (blockExists(x + 1, y, z) && (BlocksList.get(blocks[x + 1][y][z]).transparent || BlocksList.get(blocks[x + 1][y][z]).translucent))
                                || !blockExists(x - 1, y, z) || (blockExists(x - 1, y, z) && (BlocksList.get(blocks[x - 1][y][z]).transparent || BlocksList.get(blocks[x - 1][y][z]).translucent))
                                || !blockExists(x, y + 1, z) || (blockExists(x, y + 1, z) && (BlocksList.get(blocks[x][y + 1][z]).transparent || BlocksList.get(blocks[x][y + 1][z]).translucent))
                                || !blockExists(x, y - 1, z) || (blockExists(x, y - 1, z) && (BlocksList.get(blocks[x][y - 1][z]).transparent || BlocksList.get(blocks[x][y - 1][z]).translucent))
                                || !blockExists(x, y, z + 1) || (blockExists(x, y, z + 1) && (BlocksList.get(blocks[x][y][z + 1]).transparent || BlocksList.get(blocks[x][y][z + 1]).translucent))
                                || !blockExists(x, y, z - 1) || (blockExists(x, y, z - 1) && (BlocksList.get(blocks[x][y][z - 1]).transparent) || BlocksList.get(blocks[x][y][z - 1]).translucent)) {

                            boolean blocksTransparent = block.transparent;
                            boolean blocksTranslucent = block.translucent;
                            boolean renderTop = !(blockExists(x, y + 1, z) && (!BlocksList.get(blocks[x][y + 1][z]).transparent || blocksTransparent)) || blocksTranslucent || BlocksList.get(blocks[x][y + 1][z]).translucent;
                            boolean renderBottom = !(blockExists(x, y - 1, z) && (!BlocksList.get(blocks[x][y - 1][z]).transparent || blocksTransparent)) || blocksTranslucent || BlocksList.get(blocks[x][y - 1][z]).translucent;
                            boolean render1 = !(blockExists(x, y, z - 1) && (!BlocksList.get(blocks[x][y][z - 1]).transparent || blocksTransparent)) || blocksTranslucent || BlocksList.get(blocks[x][y][z - 1]).translucent;
                            boolean render2 = !(blockExists(x - 1, y, z) && (!BlocksList.get(blocks[x - 1][y][z]).transparent || blocksTransparent)) || blocksTranslucent || BlocksList.get(blocks[x - 1][y][z]).translucent;
                            boolean render3 = !(blockExists(x, y, z + 1) && (!BlocksList.get(blocks[x][y][z + 1]).transparent || blocksTransparent)) || blocksTranslucent || BlocksList.get(blocks[x][y][z + 1]).translucent;
                            boolean render4 = !(blockExists(x + 1, y, z) && (!BlocksList.get(blocks[x + 1][y][z]).transparent || blocksTransparent)) || blocksTranslucent || BlocksList.get(blocks[x + 1][y][z]).translucent;

                            blockBuilder.buildCube(block, pos,renderTop,renderBottom,render1,render2,render3,render4);
                        }
                    }
                }
            }
        }
        
        return chunkMesh == null ? blockBuilder.end(GL20.GL_STREAM_DRAW) : blockBuilder.end(chunkMesh);
    }

    public boolean blockExists(int x, int y, int z) {
        return !isOutBound(x, y, z) && blocks[x][y][z] != 0;
    }
    
    public boolean isOutBound(int x, int y, int z) {
    	return x < 0 || y < 0 || z < 0 || x >= mapWidth || y >= mapHeight || z >= mapLength;
    }
    
    public int getLight(int x, int y, int z) {
 	    return isOutBound(x, y, z) ? 0xF0 : lights[x][y][z]&0xFF;
 	}
    
    // Get the bits XXXX0000
 	public int getSunLight(int x, int y, int z) {
 	    return LightUtil.getSunLight(lights[x][y][z]);
 	}

 	// Set the bits XXXX0000
 	public void setSunLight(int x, int y, int z, int val) {
 		lights[x][y][z] = (byte)((lights[x][y][z] & 0xF) | (val << 4));
 	}

 	// Get the bits 0000XXXX
 	public int getSrcLight(int x, int y, int z) {
 	    return LightUtil.getSrcLight(lights[x][y][z]);
 	}
 	
 	// Set the bits 0000XXXX
 	public void setSrcLight(int x, int y, int z, int val) {
 		lights[x][y][z] = (byte)((lights[x][y][z] & 0xF0) | val); // Check this
 	}
    
    public Texture getTexture() {
    	return ultimate.currentTexture;
    }

	@Override
	public void dispose() {
		ultimate.dispose();
	}
}
