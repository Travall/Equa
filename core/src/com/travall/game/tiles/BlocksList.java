package com.travall.game.tiles;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class BlocksList {
    public static short Air = 0;
    public static short Bedrock = 1;
    public static short Stone = 2;
    public static short Dirt = 3;
    public static short Grass = 4;
    public static short Sand = 5;
    public static short Water = 6;


    HashMap<Short,Block> types = new HashMap<>();

    public BlocksList(Texture ultimate) {
        types.put(Bedrock,new Bedrock(ultimate));
        types.put(Stone,new Stone(ultimate));
        types.put(Dirt,new Dirt(ultimate));
        types.put(Grass,new Grass(ultimate));
        types.put(Sand,new Sand(ultimate));
        types.put(Water,new Water(ultimate));
    }

    public Block get(short id) {
        return types.get(id);
    }
}
