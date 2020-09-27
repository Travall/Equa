package com.travall.game.blocks;

import com.badlogic.gdx.utils.IntMap;
import com.travall.game.tools.UltimateTexture;

public class BlocksList {
    public static final short 
    Air = 0,
    Bedrock = 1,
    Stone = 2,
    Dirt = 3,
    Grass = 4,
    Sand = 5,
    Water = 6,
    Gold = 7,
    Log = 8,
    Leaves = 9;

    // Changed from HashMap to IntMap to avoid object baking from "short" key.
    public static final IntMap<Block> types = new IntMap<>();
    
    private static boolean hasInts;
    
    public static void ints(UltimateTexture ultimate) {
    	if (hasInts) return;
    	types.put(Bedrock,new Bedrock(ultimate));
        types.put(Stone,new Stone(ultimate));
        types.put(Dirt,new Dirt(ultimate));
        types.put(Grass,new Grass(ultimate));
        types.put(Sand,new Sand(ultimate));
        types.put(Water,new Water(ultimate));
        types.put(Gold,new Gold(ultimate));
        types.put(Log,new Log(ultimate));
        types.put(Leaves,new Leaves(ultimate));

        hasInts = true;
    }

    public static Block get(short id) {
        return types.get(id);
    }
}
