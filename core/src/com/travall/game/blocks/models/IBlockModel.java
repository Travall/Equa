package com.travall.game.blocks.models;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public interface IBlockModel {
	/** Optional static Vectors for creating bounding boxes. */
	static final Vector3 MIN = new Vector3(), MAX = new Vector3();
	
	/** Do not modify this. */
	static final Array<BoundingBox> EMPTY_BOX = new Array<BoundingBox>(0);
	
	public void build(QuadBuilder builder, @Null BlockPos position);
	public TextureRegion getDefaultTexture();
	
	/** Optional. You can add bounding boxes in Block class and it will override this.  */
	public default Array<BoundingBox> getBoundingBoxes(BlockPos pos) {
		return EMPTY_BOX;
	}
	
	/** Optional. You can change <code>isFaceSolid()</code> in Block class and it will override this.  */
	public default boolean isFaceSolid(BlockPos pos, Facing face) {
		return false;
	}
	
	/** Optional. You can change <code>isFaceSolid()</code> in Block class and it will override this.  */
	public default boolean canAddFace(BlockPos primaray, BlockPos secondary, Facing face) {
		final Block block = world.getBlock(secondary);
		if (block.isAir()) return true;
		
		final Block main = BlocksList.get(world.data[primaray.x][primaray.y][primaray.z]);
		final boolean first  =  main.isFaceSolid(primaray,  face);
		final boolean second = block.isFaceSolid(secondary, face.invert());
		
		if (first && second)
			return false;
		if (first && !second)
			return true; // primary is solid and secondary is trans.
		if (!first && second)
			return false;// primary is trans and secondary is solid.
		
		return main != block;
	}
}
