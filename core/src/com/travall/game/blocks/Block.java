package com.travall.game.blocks;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.BlockTextures;

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
