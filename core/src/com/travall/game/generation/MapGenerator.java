package com.travall.game.generation;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.tiles.*;
import com.travall.game.tools.*;


public class MapGenerator {
    int mapWidth;
    int mapLength;
    int mapHeight;
    int waterLevel;
    public short[][][] blocks;
    public byte[][][] lightMap;
    public BlocksList blockList;
    BlockModel tileModel;
    ModelCache tileCache;
    Vector3 temp = new Vector3();
    Texture ultimate = new Texture("Tiles/ultimate3.png");

    public MapGenerator(int mapWidth, int mapHeight, int mapLength, int waterLevel) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapLength = mapLength;
        this.waterLevel = waterLevel;
        this.tileModel = generateModel();
        this.tileCache = new ModelCache();
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


    public ModelCache generateShell(int indexX, int indexZ,int chunkSizeX, int chunkSizeZ) {
        tileCache = new ModelCache();
        tileCache.begin();
        Block block;
        for(int x = indexX; x < indexX + chunkSizeX; x++) {
            for(int y = 0; y < blocks[0].length; y++) {
                for(int z = indexZ; z < indexZ + chunkSizeZ; z++) {
                    if(blockExists(x,y,z)) {
                        temp.set(x,y,z);

                        block = blockList.get(blocks[x][y][z]);

                        boolean blocksTranslucent = block.transparent;
                        if(!blockExists(x + 1, y, z) || (blockExists(x + 1, y, z) && blockList.get(blocks[x+1][y][z]).transparent)
                                || !blockExists(x - 1, y, z) || (blockExists(x - 1, y, z) && blockList.get(blocks[x-1][y][z]).transparent)
                                || !blockExists(x, y + 1, z) || (blockExists(x, y + 1, z) && blockList.get(blocks[x][y + 1][z]).transparent)
                                || !blockExists(x, y - 1, z) || (blockExists(x, y - 1, z) && blockList.get(blocks[x][y - 1][z]).transparent)
                                || !blockExists(x, y, z + 1) || (blockExists(x, y, z + 1) && blockList.get(blocks[x][y][z + 1]).transparent)
                                || !blockExists(x, y, z - 1) || (blockExists(x, y, z - 1) && blockList.get(blocks[x][y][z - 1]).transparent)) {

                            boolean renderTop = !(blockExists(x, y + 1, z) && (!blockList.get(blocks[x][y + 1][z]).transparent || blocksTranslucent));
                            boolean renderBottom = !(blockExists(x, y - 1, z) && (!blockList.get(blocks[x][y - 1][z]).transparent || blocksTranslucent));
                            boolean render1 = !(blockExists(x, y, z - 1) && (!blockList.get(blocks[x][y][z - 1]).transparent || blocksTranslucent));
                            boolean render2 = !(blockExists(x - 1, y, z) && (!blockList.get(blocks[x - 1][y][z]).transparent || blocksTranslucent));
                            boolean render3 = !(blockExists(x, y, z + 1) && (!blockList.get(blocks[x][y][z + 1]).transparent || blocksTranslucent));
                            boolean render4 = !(blockExists(x + 1, y, z) && (!blockList.get(blocks[x + 1][y][z]).transparent || blocksTranslucent));

                            BlockInstance instance = getModelInstance(block, temp,renderTop,renderBottom,render1,render2,render3,render4,lightMap[x][y][z]);


                            tileCache.add(instance);
                        }
                    }
                }
            }
        }
        tileCache.end();


        return tileCache;
    }

    public boolean blockExists(int x, int y, int z) {
        return x >= 0 && x < blocks.length && y >=0 && y < blocks[x].length && z >= 0 && z < blocks[x][y].length && blocks[x][y][z] != 0;
    }


    private BlockInstance getModelInstance(Block block, Vector3 position, boolean renderTop, boolean renderBottom, boolean render1, boolean render2, boolean render3, boolean render4, float factor) {

        if(factor == 0) factor = 1;
        tileModel.materials.first().set(TextureAttribute.createDiffuse(block.texture));
        tileModel.materials.first().set(ColorAttribute.createDiffuse(factor/8,factor/8,factor/8,1));
        BlendingAttribute ba = Utils.ba;
        if(block.transparent) {
            ba.blended = true;
        } else {
            ba.blended = false;
        }
        tileModel.materials.first().set(ba);

        tileModel.nodes.first().parts.get(0).enabled = renderTop;
        tileModel.nodes.first().parts.get(1).enabled = renderBottom;
        tileModel.nodes.first().parts.get(2).enabled = render1;
        tileModel.nodes.first().parts.get(3).enabled = render2;
        tileModel.nodes.first().parts.get(4).enabled = render3;
        tileModel.nodes.first().parts.get(5).enabled = render4;

        BlockInstance instance = new BlockInstance(tileModel);
        instance.transform.setTranslation(position.x,position.y,position.z);

        return instance;
    }

    private BlockModel generateModel() {
        BlockModelBuilder modelBuilder = new BlockModelBuilder();
        modelBuilder.begin();
        Texture text = new Texture("Tiles/unknown.png");
        Material mat = new Material(TextureAttribute.createDiffuse(text));
//		mat.set(IntAttribute.createCullFace(GL20.GL_NONE));

        MeshPartBuilder mpb = modelBuilder.part("top", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,mat);

        //top
        mpb.setUVRange(new TextureRegion(text,16,16,16,16));
        Vector3 first = new Vector3(0,1f,0);
        Vector3 second = new Vector3(0,1f,1);
        Vector3 third = new Vector3(1,1f,1);
        Vector3 fourth = new Vector3(1,1f,0);
        Vector3 normal = new Vector3(0,1,0);
        mpb.rect(first,second,third,fourth,normal);

        mpb = modelBuilder.part("bottom", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,mat);

        //bottom
        mpb.setUVRange(new TextureRegion(text,16,48,16,16));
        first.set(1,0,0);
        second.set(1,0,1);
        third.set(0,0,1);
        fourth.set(0,0,0);
        normal.set(0,-1,0);
        mpb.rect(first,second,third,fourth,normal);

        mpb = modelBuilder.part("1", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,mat);

        mpb.setUVRange(new TextureRegion(text,16,32,16,16));
        first.set(0,0,0);
        second.set(0,1f,0);
        third.set(1,1f,0);
        fourth.set(1,0,0);
        normal.set(0,0,-1);
        mpb.rect(fourth,first,second,third,normal);

        mpb = modelBuilder.part("2", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,mat);

        mpb.setUVRange(new TextureRegion(text,0,16,16,16));
        first.set(0,1f,1);
        second.set(0,1f,0);
        third.set(0,0,0);
        fourth.set(0,0f,1);
        normal.set(-1,0,0);
        mpb.rect(fourth,first,second,third,normal);

        mpb = modelBuilder.part("3", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,mat);

        mpb.setUVRange(new TextureRegion(text,16,0,16,16));
        first.set(0,1f,1);
        second.set(0,0f,1);
        third.set(1,0f,1);
        fourth.set(1,1f,1);
        normal.set(0,0,1);
        mpb.rect(fourth,first,second,third,normal);

        mpb = modelBuilder.part("4", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,mat);

        TextureRegion textureRegion = new TextureRegion(text,32,16,16,16);
        textureRegion.flip(true,false);
        mpb.setUVRange(textureRegion);

        first.set(1,1f,0);
        second.set(1,1f,1);
        third.set(1,0f,1);
        fourth.set(1,0f,0);
        normal.set(1,0,0);

        mpb.rect(fourth,first,second,third,normal);

        return modelBuilder.end();
    }
}
