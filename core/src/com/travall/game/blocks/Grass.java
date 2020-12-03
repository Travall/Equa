package com.travall.game.blocks;

import static com.travall.game.renderer.block.UltimateTexture.createRegion;

import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.CubeModel;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.ui.actors.BlockSeletion;
import com.travall.game.utils.ArrayDir;
import com.travall.game.utils.BlockPos;

public class Grass extends Block {
	
	public static final int 
	NORMAL_GRASS = 0,
	FLAX_GRASS = 1,
	SNOW_GRASS = 2;

	public Grass(int blockID) {
		super(blockID);
		
		newTypeComponent(3);
		final ArrayDir<BlockTextures> textures = new ArrayDir<>(BlockTextures.class, 3);
		textures.put(NORMAL_GRASS, new BlockTextures(createRegion(4, 0), createRegion(3, 0), createRegion(2, 0)));
		textures.put(FLAX_GRASS, new BlockTextures(createRegion(4, 1), createRegion(3, 1), createRegion(2, 0)));
		textures.put(SNOW_GRASS,   new BlockTextures(createRegion(6, 0), createRegion(5, 0), createRegion(2, 0)));
		this.model = new CubeModel(this, textures);
		this.material = Material.BLOCK;
		
		BlockSeletion.add(this, NORMAL_GRASS);
		BlockSeletion.add(this, FLAX_GRASS);
		BlockSeletion.add(this, SNOW_GRASS);
	}
	
	@Override
	public String getName(BlockPos pos, int data) {
		if (pos != null) {
			return getName(getType(pos));
		}
		
		return getName(data);
	}
	
	private String getName(int type) {
		switch (type) {
		case NORMAL_GRASS: return "Grass Block";
		case FLAX_GRASS: return "Flax Grass Block";
		case SNOW_GRASS: return "Snow Grass Block";
		default: return "unknown";
		}
	}
}
