package com.travall.game.generation;

import java.util.Random;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.Main;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.tools.BlockBuilder;
import com.travall.game.tools.ChunkMesh;
import com.travall.game.tools.FloodLight;
import com.travall.game.tools.OpenSimplexOctaves;
import com.travall.game.tools.UltimateTexture;
import com.travall.game.tools.Utils;


public class MapGenerator implements Disposable {
    public final int mapWidth;
    public final int mapLength;
    public final int mapHeight;
    int waterLevel;
    public short[][][] blocks;
    private byte[][][] lights;
    BlockBuilder blockBuilder;
    FloodLight floodLight;
    Vector3 temp = new Vector3();
    public UltimateTexture ultimate;
	GridPoint3 pos = new GridPoint3();

    public MapGenerator(Main main, int mapWidth, int mapHeight, int mapLength, int waterLevel) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapLength = mapLength;
        this.waterLevel = waterLevel;
        this.blockBuilder = new BlockBuilder();
        this.floodLight = new FloodLight(this, main);
        this.ultimate = new UltimateTexture(new Texture("Tiles/ultimate3.png"));
        BlocksList.ints(ultimate);
        generate();
    }

    private void generate() {
    	final Random random = MathUtils.random;
        OpenSimplexOctaves MountainNoise = new OpenSimplexOctaves(7,0.43, random.nextInt()); // changed from 0.45 to 0.43
        OpenSimplexOctaves CaveNoise = new OpenSimplexOctaves(5,0.25, random.nextInt());
        OpenSimplexOctaves FlatNoise = new OpenSimplexOctaves(6,0.2, random.nextInt()); // changed from 0.15 to 0.2
        OpenSimplexOctaves DecisionNoise = new OpenSimplexOctaves(8,0.02, random.nextInt());

        blocks = new short[mapWidth][mapHeight][mapLength];
        lights = new byte[mapWidth][mapHeight][mapLength];

        int maxTerrainHeight = mapHeight / 2;

        for(int x = 0; x < mapWidth; x++) {
            for(int z = 0; z < mapLength; z++) {
                double mountain = Utils.normalize(MountainNoise.getNoise(x,z),maxTerrainHeight) * 1.4;
                double flat = Utils.normalize(FlatNoise.getNoise(x,z),maxTerrainHeight) *0.6f;
                double decision = Utils.normalize(DecisionNoise.getNoise(x,z),1);

                float steep = Interpolation.exp10.apply(Interpolation.exp5.apply((Interpolation.exp10.apply((float) decision))));

                float height = MathUtils.lerp((float)flat, (float)mountain, steep);


                int yValue = (Math.round(height / 1) * 1);

//                for(int j = waterLevel; j > 0; j--) {
//                    if (!blockExists(x, j, z)) {
//                        blocks[x][j][z] = BlocksList.Water;
//                    } else {
//                        break;
//                    }
//                }

                for(int i = yValue; i >= 0; i--) {
                    double caves = Utils.normalize(CaveNoise.getNoise(x, (int) (i), z), maxTerrainHeight);
                    boolean caveTerritory = (caves >= maxTerrainHeight - (height - i) * 4 && caves > maxTerrainHeight /1.9 && i > 0);
                    if(i == 0) {
                        blocks[x][i][z] = BlocksList.Bedrock;
                    } else {
                        if (i == yValue && i >= waterLevel) {
                            if (Math.abs(i - waterLevel) < 3 || steep < 0.6) {
                                blocks[x][i][z] = BlocksList.Sand;
                            } else {
                                blocks[x][i][z] = BlocksList.Grass;
                            }
                        } else {
                            // steep < 0.65 && Math.abs(yValue - i) < (steep * 12) + 2
                            if (Math.abs(i - waterLevel) < 3 && steep < 0.6) {
                                blocks[x][i][z] = BlocksList.Sand;
                            } else if(!caveTerritory) {
                                if(caves >= mapHeight/2 - (height - i) * 5) {
                                    blocks[x][i][z] = BlocksList.Stone;
                                } else {
                                    blocks[x][i][z] = BlocksList.Dirt;
                                }

                            }
                        }
                    }
                }

                for(int j = waterLevel; j > 0; j--) {
                    double caves = Utils.normalize(CaveNoise.getNoise(x, (int) (j), z), maxTerrainHeight);
                    boolean caveTerritory = (caves >= maxTerrainHeight - (height - j) * 4 && caves > maxTerrainHeight /1.9 && j > 0);
                    if (blocks[x][j][z] == BlocksList.Air && !caveTerritory) {
                        blocks[x][j][z] = BlocksList.Water;
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


    public ChunkMesh generateShell(int indexX, int indexZ,int chunkSizeX, int chunkSizeZ, ChunkMesh chunkMesh) {
    	blockBuilder.begin();
        Block block;
        for(int x = indexX; x < indexX + chunkSizeX; x++) {
            for(int y = 0; y < blocks[0].length; y++) {
                for(int z = indexZ; z < indexZ + chunkSizeZ; z++) {
                    if(blockExists(x,y,z)) {
                        pos.set(x,y,z);
                        block = BlocksList.get(blocks[x][y][z]);
                        
                        if  (!blockExists(x + 1, y, z) || (blockExists(x + 1, y, z) && BlocksList.get(blocks[x + 1][y][z]).transparent)
                          || !blockExists(x - 1, y, z) || (blockExists(x - 1, y, z) && BlocksList.get(blocks[x - 1][y][z]).transparent)
                          || !blockExists(x, y + 1, z) || (blockExists(x, y + 1, z) && BlocksList.get(blocks[x][y + 1][z]).transparent)
                          || !blockExists(x, y - 1, z) || (blockExists(x, y - 1, z) && BlocksList.get(blocks[x][y - 1][z]).transparent)
                          || !blockExists(x, y, z + 1) || (blockExists(x, y, z + 1) && BlocksList.get(blocks[x][y][z + 1]).transparent)
                          || !blockExists(x, y, z - 1) || (blockExists(x, y, z - 1) && BlocksList.get(blocks[x][y][z - 1]).transparent)) {

                        	boolean blocksTranslucent = block.transparent;
                            boolean renderTop = !(blockExists(x, y + 1, z) && (!BlocksList.get(blocks[x][y + 1][z]).transparent || blocksTranslucent));
                            boolean renderBottom = !(blockExists(x, y - 1, z) && (!BlocksList.get(blocks[x][y - 1][z]).transparent || blocksTranslucent));
                            boolean render1 = !(blockExists(x, y, z - 1) && (!BlocksList.get(blocks[x][y][z - 1]).transparent || blocksTranslucent));
                            boolean render2 = !(blockExists(x - 1, y, z) && (!BlocksList.get(blocks[x - 1][y][z]).transparent || blocksTranslucent));
                            boolean render3 = !(blockExists(x, y, z + 1) && (!BlocksList.get(blocks[x][y][z + 1]).transparent || blocksTranslucent));
                            boolean render4 = !(blockExists(x + 1, y, z) && (!BlocksList.get(blocks[x + 1][y][z]).transparent || blocksTranslucent));

                            blockBuilder.buildCube(block, pos,renderTop,renderBottom,render1,render2,render3,render4,this);
                        }
                    }
                }
            }
        }
        
        if (chunkMesh == null) {
        	return blockBuilder.end(GL20.GL_STREAM_DRAW);
        }
        return blockBuilder.end(chunkMesh);
    }

    public boolean blockExists(int x, int y, int z) {
        return !isOutBound(x, y, z) && blocks[x][y][z] != 0;
    }
    
    public boolean isOutBound(int x, int y, int z) {
    	return x < 0 || y < 0 || z < 0 || x >= mapWidth || y >= mapHeight || z >= mapLength;
    }
    
 // Get the bits XXXX0000
 	public int getSunLight(int x, int y, int z) {
 	    return (lights[x][y][z] >>> 4) & 0xF;
 	}

 	// Set the bits XXXX0000
 	public void setSunLight(int x, int y, int z, int val) {
 		lights[x][y][z] = (byte)((lights[x][y][z] & 0xF) | (val << 4));
 	}

 	// Get the bits 0000XXXX
 	public int getSrcLight(int x, int y, int z) {
 	    return lights[x][y][z] & 0xF;
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
