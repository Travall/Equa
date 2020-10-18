package com.travall.game.blocks;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.travall.game.blocks.data.DataManager;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.IBlockModel;
import com.travall.game.entities.Player;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.utils.AmbiantType;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;
import com.travall.game.utils.math.CollisionBox;

public class Block {
	protected static final Vector3 MIN = new Vector3(), MAX = new Vector3();
	protected static final Array<BoundingBox> CUBE_BOX;

	static {
		CUBE_BOX = new Array<BoundingBox>(1);
		CUBE_BOX.add(new BoundingBox(MIN.set(0, 0, 0), MAX.set(1, 1, 1)));
	}

	/** Must be in between 0-15 (optional) */
	protected int lightLevel;
	/** Material of this block. (required) */
	protected Material material;
	/** Bounding boxes of this block. (might create one if this block is not a full-block).
	 * It can have bounding boxes here or at model, but here will override the model's bounding boxes. */
	protected final Array<BoundingBox> boundingBoxes = new Array<BoundingBox>(4);
	/** Model of this block. (required) */
	protected IBlockModel model;
	
	/** Block data manager. (optional) */
	protected final DataManager manager = new DataManager();

	/** Cashes the class's name. */
	private final String name = getClass().getSimpleName();
	
	/** ID of this block. */
	protected final int ID;

	protected Block(final int blockID) {
		this.ID = blockID;
	}
	
	/** Get the name of this block. */
	public String getName() {
		return name;
	}

	/** Get material of this block. */
	public final Material getMaterial() {
		return material;
	}
	
	/** Get model of this block. */
	public IBlockModel getBlockModel() {
		return model;
	}
	
	/** Can this block add from secondary block. */
	public boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		final Block block = world.getBlock(secondary);
		if (block.isAir()) return true;
		
		final boolean first  =  this.isFaceSolid(primaray,  face);
		final boolean second = block.isFaceSolid(secondary, face.invert());
		
		if (first && second)
			return false;
		if (first && !second)
			return true; // primary is solid and secondary is trans.
		if (!first && second)
			return false;// primary is trans and secondary is solid.
		
		return this != block;
	}
	
	/** Is this block has solid face. */
	public boolean isFaceSolid(BlockPos pos, Facing face) {
		return material.isSolid();
	}

	/** Get bounding boxes of this block. */
	public Array<BoundingBox> getBoundingBoxes(BlockPos pos) {
		return material.isFullCube() ? CUBE_BOX : boundingBoxes.isEmpty() ? model.getBoundingBoxes(pos) : boundingBoxes;
	}
	
	/** For collision detection. */
	public void addCollisions(BlockPos pos, Array<CollisionBox> boxes, Pool<CollisionBox> pool) {
		if (!material.hasCollision()) return;
		
		final Array<BoundingBox> boundingBoxes = getBoundingBoxes(pos);
		if (boundingBoxes.isEmpty()) return;
			
		if (boundingBoxes.size == 1) {
			boxes.add(pool.obtain().set(boundingBoxes.get(0)).move(pos.x, pos.y, pos.z));
		} else for (BoundingBox box : boundingBoxes) {
			boxes.add(pool.obtain().set(box).move(pos.x, pos.y, pos.z));
		}
	}
	
	/** Call when entity collide to the block. */
	public void onEntityCollide(BlockPos pos) {
		
	}
	
	/** Call when player click on the block. 
	 * @return true to stop placing the block (onPlace). */
	public boolean onClick(Player player, RayInfo rayInfo, int button) {
		return false;
	}
	
	/** Place the block. 
	 *  @return true if player has successfully place the block. */
	public boolean onPlace(Player player, RayInfo rayInfo) {	
		if (world.isAirBlock(rayInfo.out.x, rayInfo.out.y, rayInfo.out.z)) {
			world.placeBlock(rayInfo.out, this);
			return true;
		}
		return false;
	}

	/** Destroy the block.
	 *  @return true if player has successfully destroy the block. */
	public boolean onDestroy(Player player, BlockPos pos) {
		world.breakBlock(pos);
		return true;
	}
	
	/** Is this block contains data. */
	public boolean hasData() {
		return !manager.isEmpty();
	}
	
	/** Get block's data component manager. */
	public DataManager getData() {
		return manager;
	}
	
	public AmbiantType getAmbiantType() {
		if (isSrclight()) return AmbiantType.FULLBRIGHT;
		return material.canBlockLights() ? AmbiantType.DARKEN : AmbiantType.NONE;
	}

	/** Get source light level of this block. */
	public final int getLightLevel() {
		return lightLevel;
	}

	/** Is this a source light block. */
	public final boolean isSrclight() {
		return lightLevel != 0;
	}
	
	/** Get this block ID. */
	public final int getID() {
		return ID;
	}
	
	/** Is this block is air. */
	public boolean isAir() {
		return false;
	}
}
