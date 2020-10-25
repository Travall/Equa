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
import com.travall.game.utils.UpdateState;
import com.travall.game.utils.math.CollisionBox;
import com.travall.game.world.lights.LightHandle;

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
		return model.canAddFace(primaray, secondary, face);
	}
	
	/** Is this block has solid face. */
	public boolean isFaceSolid(BlockPos pos, Facing face) {
		return material.isSolid() || model.isFaceSolid(pos, face);
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
			return onPlace(rayInfo.out);
		}
		return false;
	}

	/** Destroy the block.
	 *  @return true if player has successfully destroy the block. */
	public final boolean onDestroy(Player player, RayInfo rayInfo) {
		return rayInfo.blockHit.onDestroy(rayInfo.in);
	}
	
	/** Place the block. 
	 *  @return true if has successfully place the block. */
	public boolean onPlace(BlockPos pos) {
		world.setBlock(pos.x, pos.y, pos.z, this);
		
		updateNearByBlocks(pos, UpdateState.ON_PLACE);
		handleLights(pos, UpdateState.ON_PLACE);
		
		world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
		return true;
	}

	/** Destroy the block.
	 *  @return true if has successfully destroy the block. */
	public boolean onDestroy(BlockPos pos) {
		world.getBlock(pos).handleLights(pos, UpdateState.ON_BREAK);
		
		world.setBlock(pos.x, pos.y, pos.z, BlocksList.AIR);
		updateNearByBlocks(pos, UpdateState.ON_BREAK);
		
		world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
		return true;
	}
	
	/** Handle the lights.*/
	protected final void handleLights(BlockPos pos, UpdateState state) {
		if (state == UpdateState.ON_PLACE) {
			if (isSrclight()) { // if place srclight block.
				LightHandle.newSrclightAt(pos.x, pos.y, pos.z, getLightLevel());
			} else { // if place non-srclight block.
				LightHandle.delSrclightAt(pos.x, pos.y, pos.z);
			}
			
			if (material.canBlockLights() || material.canBlockSunRay()) {
				LightHandle.newRaySunlightAt(pos.x, pos.y, pos.z);
				LightHandle.delSunlightAt(pos.x, pos.y, pos.z);
			}
		} else
		if (state == UpdateState.ON_BREAK) {
			final Block block = world.getBlock(pos);
			if (block.isSrclight()) { // if break srclight block.
				LightHandle.delSrclightAt(pos.x, pos.y, pos.z);
			} else { // if break non-srclight block.
				LightHandle.newSrclightShellAt(pos.x, pos.y, pos.z);
			}

			if (block.material.canBlockLights() || block.material.canBlockSunRay()) {
				LightHandle.newRaySunlightAt(pos.x, pos.y, pos.z);
				LightHandle.newSunlightShellAt(pos.x, pos.y, pos.z);
			}
		}
	}
	
	/** Add doc plz. */
	public void updateNearByBlocks(BlockPos pos, UpdateState state) {
		BlockPos offset;
		
		offset = pos.offset(Facing.UP);
		world.getBlock(offset).onNeighbourUpdate(offset, pos, Facing.UP, state);
		
		offset = pos.offset(Facing.DOWN);
		world.getBlock(offset).onNeighbourUpdate(offset, pos, Facing.DOWN, state);
		
		offset = pos.offset(Facing.NORTH);
		world.getBlock(offset).onNeighbourUpdate(offset, pos, Facing.NORTH, state);
		
		offset = pos.offset(Facing.EAST);
		world.getBlock(offset).onNeighbourUpdate(offset, pos, Facing.EAST, state);
		
		offset = pos.offset(Facing.SOUTH);
		world.getBlock(offset).onNeighbourUpdate(offset, pos, Facing.SOUTH, state);
		
		offset = pos.offset(Facing.WEST);
		world.getBlock(offset).onNeighbourUpdate(offset, pos, Facing.WEST, state);
	}
	
	/** Add doc plz. */
	public void onNeighbourUpdate(BlockPos primaray, BlockPos secondary, Facing face, UpdateState state) {
		
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
