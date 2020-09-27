package com.travall.game.blocks;

import com.badlogic.gdx.utils.IntMap;
import com.travall.game.tools.UltimateTexture;

public class BlocksList {
    // Changed from HashMap to IntMap to avoid object baking from "short" key.
    public static final IntMap<Block> types = new IntMap<>();
    
    private static boolean hasInts;
    
    public static void ints(UltimateTexture ultimate) {
    	if (hasInts) return;
    	types.put(Bedrock.id,new Bedrock(ultimate));
        types.put(Stone.id,new Stone(ultimate));
        types.put(Dirt.id,new Dirt(ultimate));
        types.put(Grass.id,new Grass(ultimate));
        types.put(Sand.id,new Sand(ultimate));
        types.put(Water.id,new Water(ultimate));
        types.put(Gold.id,new Gold(ultimate));
        types.put(Log.id,new Log(ultimate));
        types.put(Leaves.id,new Leaves(ultimate));

        hasInts = true;
    }

    public static Block get(short id) {
        return types.get(id);
    }
}
