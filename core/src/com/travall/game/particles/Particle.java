package com.travall.game.particles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.math.CollisionBox;
import com.travall.game.world.World;

public abstract class Particle {
	
	private static final CollisionBox bintersector = new CollisionBox();
	private static final BlockPos blockPos = new BlockPos();
	private static final Array<CollisionBox> boxes = new Array<>(6);

	public final CollisionBox box = new CollisionBox();
	public final Vector3 velocity = new Vector3();
	public final TextureRegion region = new TextureRegion();
	public float size = 0.1f;
	protected boolean onGround;
	
	protected abstract void update(World world);
	
	protected abstract boolean isDead();
	
	protected final void move(World world) {
		
		float
		xMove = velocity.x,
		yMove = velocity.y,
		zMove = velocity.z;
		
		
		box.expand(xMove, yMove, zMove, bintersector);

		final int xMin = MathUtils.floor(bintersector.xMin);
		final int yMin = MathUtils.floor(bintersector.yMin);
		final int zMin = MathUtils.floor(bintersector.zMin);
		final int xMax = MathUtils.ceil(bintersector.xMax);
		final int yMax = MathUtils.ceil(bintersector.yMax);
		final int zMax = MathUtils.ceil(bintersector.zMax);
		
		for (int x = xMin; x < xMax; x++)
		for (int y = yMin; y < yMax; y++)
		for (int z = zMin; z < zMax; z++) {
			world.getBlock(blockPos.set(x, y, z)).addCollisions(blockPos, boxes, CollisionBox.POOL);
		}
		
		final float 
		xLastMove = xMove,
		yLastMove = yMove,
		zLastMove = zMove;

		final int size = boxes.size;
		for (int i = 0; i < size; i++) {
			yMove = boxes.get(i).collideY(box, yMove);
		}
		box.yMin += yMove;
		box.yMax += yMove;
		
		onGround = (yLastMove != yMove) && yLastMove < 0f;
		
		for (int i = 0; i < size; i++) {
			xMove = boxes.get(i).collideX(box, xMove);
		}
		box.xMin += xMove;
		box.xMax += xMove;
		
		for (int i = 0; i < size; i++) {
			zMove = boxes.get(i).collideZ(box, zMove);
		}
		box.zMin += zMove;
		box.zMax += zMove;
		
				
		if (xLastMove != xMove) {
			velocity.x = 0f;
		}
		if (yLastMove != yMove) {
			velocity.y = 0f;
		}
		if (zLastMove != zMove) {
			velocity.z = 0f;
		}

		CollisionBox.POOL.freeAll(boxes);
		boxes.size = 0;
	}
	
	public final Vector3 getPosition(Vector3 out) {
		out.x = (box.xMin + box.xMax) * 0.5f;
		out.y = (box.yMin + box.yMax) * 0.5f;
		out.z = (box.zMin + box.zMax) * 0.5f;
		return out;
	}
	
	public final void setPosition(Vector3 pos, float size) {
		box.set(pos.x - size, pos.y - size, pos.z - size, pos.x + size, pos.y + size, pos.z + size);
	}
}
