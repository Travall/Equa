package com.travall.game.blocks.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.utils.BlockPos;

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
}
