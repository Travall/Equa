package com.travall.game.generation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.tools.BlockBuilder;
import com.travall.game.tools.BlockBuilder.VertInfo;
import com.travall.game.tools.ChunkMesh;
import com.travall.game.tools.OpenSimplexOctaves;
import com.travall.game.tools.UltimateTexture;
import com.travall.game.tools.Utils;


public class MapGenerator implements Disposable {
    int mapWidth;
    int mapLength;
    int mapHeight;
    int waterLevel;
    public short[][][] blocks;
    public byte[][][] lightMap;
    public BlocksList blockList;
    BlockBuilder blockBuilder;
    Vector3 temp = new Vector3();
    public UltimateTexture ultimate;
	GridPoint3 pos = new GridPoint3();

    public MapGenerator(int mapWidth, int mapHeight, int mapLength, int waterLevel) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapLength = mapLength;
        this.waterLevel = waterLevel;
        this.blockBuilder = new BlockBuilder();
        this.ultimate = new UltimateTexture(new Texture("Tiles/ultimate3.png"));
        this.blockList = new BlocksList(ultimate);
        generate();
    }

    private void generate() {
        OpenSimplexOctaves MountainNoise = new OpenSimplexOctaves(7,0.45, (int) (Math.random() * 100000));
        OpenSimplexOctaves CaveNoise = new OpenSimplexOctaves(5,0.25, (int) (Math.random() * 100000));
        OpenSimplexOctaves FlatNoise = new OpenSimplexOctaves(6,0.15, (int) (Math.random() * 100000));
        OpenSimplexOctaves DecisionNoise = new OpenSimplexOctaves(8,0.02, (int) (Math.random() * 100000));

        blocks = new short[mapWidth][mapHeight][mapLength];
        lightMap = new byte[mapWidth][mapHeight][mapLength];

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

    public void placeLight(Vector3 position) {
        int distance = 8;

        for(int xx = (int) (position.x - distance); xx < position.x + distance; xx++) {
            for(int yy = (int) (position.y - distance); yy < position.y + distance; yy++) {
                for(int zz = (int) (position.z - distance); zz < position.z + distance; zz++) {
                    if(blockExists(xx,yy,zz)) {
                        if(!blockExists(xx + 1, yy, zz) || (blockExists(xx + 1, yy, zz) && blockList.get(blocks[xx+1][yy][zz]).transparent)
                                || !blockExists(xx - 1, yy, zz) || (blockExists(xx - 1, yy, zz) && blockList.get(blocks[xx-1][yy][zz]).transparent)
                                || !blockExists(xx, yy + 1, zz) || (blockExists(xx, yy + 1, zz) && blockList.get(blocks[xx][yy + 1][zz]).transparent)
                                || !blockExists(xx, yy - 1, zz) || (blockExists(xx, yy - 1, zz) && blockList.get(blocks[xx][yy - 1][zz]).transparent)
                                || !blockExists(xx, yy, zz + 1) || (blockExists(xx, yy, zz + 1) && blockList.get(blocks[xx][yy][zz + 1]).transparent)
                                || !blockExists(xx, yy, zz - 1) || (blockExists(xx, yy, zz - 1) && blockList.get(blocks[xx][yy][zz - 1]).transparent)) {

                            lightMap[xx][yy][zz] =  (byte) (distance - position.dst(xx,yy,zz));
                        }
                    }
                }
            }
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
                        block = blockList.get(blocks[x][y][z]);
                        
                        if  (!blockExists(x + 1, y, z) || (blockExists(x + 1, y, z) && blockList.get(blocks[x + 1][y][z]).transparent)
                          || !blockExists(x - 1, y, z) || (blockExists(x - 1, y, z) && blockList.get(blocks[x - 1][y][z]).transparent)
                          || !blockExists(x, y + 1, z) || (blockExists(x, y + 1, z) && blockList.get(blocks[x][y + 1][z]).transparent)
                          || !blockExists(x, y - 1, z) || (blockExists(x, y - 1, z) && blockList.get(blocks[x][y - 1][z]).transparent)
                          || !blockExists(x, y, z + 1) || (blockExists(x, y, z + 1) && blockList.get(blocks[x][y][z + 1]).transparent)
                          || !blockExists(x, y, z - 1) || (blockExists(x, y, z - 1) && blockList.get(blocks[x][y][z - 1]).transparent)) {

                        	boolean blocksTranslucent = block.transparent;
                            boolean renderTop = !(blockExists(x, y + 1, z) && (!blockList.get(blocks[x][y + 1][z]).transparent || blocksTranslucent));
                            boolean renderBottom = !(blockExists(x, y - 1, z) && (!blockList.get(blocks[x][y - 1][z]).transparent || blocksTranslucent));
                            boolean render1 = !(blockExists(x, y, z - 1) && (!blockList.get(blocks[x][y][z - 1]).transparent || blocksTranslucent));
                            boolean render2 = !(blockExists(x - 1, y, z) && (!blockList.get(blocks[x - 1][y][z]).transparent || blocksTranslucent));
                            boolean render3 = !(blockExists(x, y, z + 1) && (!blockList.get(blocks[x][y][z + 1]).transparent || blocksTranslucent));
                            boolean render4 = !(blockExists(x + 1, y, z) && (!blockList.get(blocks[x + 1][y][z]).transparent || blocksTranslucent));

                            blockBuilder.buildCube(block, pos,renderTop,renderBottom,render1,render2,render3,render4,lightMap[x][y][z]);
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
        return x >= 0 && x < blocks.length && y >=0 && y < blocks[x].length && z >= 0 && z < blocks[x][y].length && blocks[x][y][z] != 0;
    }
    
    public Texture getTexture() {
    	return ultimate.currentTexture;
    }

	@Override
	public void dispose() {
		ultimate.dispose();
	}
}
