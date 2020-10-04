package com.travall.game.blocks;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class Block {
	protected static final Vector3 min = new Vector3(), max = new Vector3();
	protected static final Array<BoundingBox> cube;

	static {
		cube = new Array<BoundingBox>(1);
		cube.add(new BoundingBox(min.set(0, 0, 0), max.set(1, 1, 1)));
	}

	/** Textures of this block. */
	protected BlockTextures textures;
	/** Must be in between 0-15 */
	protected int lightLevel;
	/** Material of this block. */
	protected Material material;
	/** Bounding boxes of this block. */
	protected Array<BoundingBox> boundingBoxes = new Array<BoundingBox>(4);

	/** Cashes the class's name. */
	private final String name = getClass().getSimpleName();

	protected Block() {
		
	}
	
	/** Get the name of this block. */
	public String getName() {
		return name;
	}

	/** Get block textures. */
	public BlockTextures getTextures() {
		return textures;
	}

	/** Get material of this block. */
	public Material getMaterial() {
		return material;
	}
	
	/** Can this block add from secondary. */
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		final Block block = world.getBlock(secondary);
		if (block == BlocksList.AIR)
			return true;
		
		final Material material = block.getMaterial();
		if (this.material.isSolid() == material.isSolid())
			return false;
		if (this.material.isSolid() == true  && material.isSolid() == false)
			return true; // primary is solid and secondary is trans.
		if (this.material.isSolid() == false && material.isSolid() == true)
			return false;// primary is trans and secondary is solid.
		
		return false;
	}
	
	/** Is this block has solid face. */
	public boolean isFaceSolid(BlockPos pos, Facing face) {
		return material.isSolid();
	}

	/** Get bounding boxes of this block. */
	public Array<BoundingBox> getBoundingBoxes() {
		return material.isFullCube() ? cube : boundingBoxes;
	}

	/** Get source light level of this block. */
	public int getLightLevel() {
		return lightLevel;
	}

	/** Is this a source light block. */
	public boolean isSrclight() {
		return lightLevel != 0;
	}
}
