package com.travall.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.Block;
import com.travall.game.handles.FirstPersonCameraController;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.math.CollisionBox;
import com.travall.game.world.World;

public class Player {

	final CollisionBox collisionBox = new CollisionBox();

	final Vector3 position = new Vector3();
	final Vector3 velocity = new Vector3();
	final Vector3 acceleration = new Vector3();

	public boolean onGround;
	public boolean isFlying = false;
	public boolean isWalking = false;

	public Player(Vector3 position) {
		setPosition(position);
	}

	public void applyForce(Vector3 force) {
		acceleration.add(force);
	}

	public void reset() {
		velocity.setZero();
		acceleration.setZero();
	}

	public void setPosition(Vector3 pos) {
		position.set(pos);
		final float w = 0.3f;
		final float h = 0.9f;
		collisionBox.set(pos.x - w, pos.y - h, pos.z - w, pos.x + w, pos.y + h, pos.z + w);
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public void update(World world, Camera camera, FirstPersonCameraController cameraController) {
		process(camera, cameraController);
		velocity.add(acceleration);
		move(world, this.velocity.x, this.velocity.y, this.velocity.z);
		acceleration.setZero();



		velocity.x = isFlying ? MathUtils.lerp(this.velocity.x, 0, 0.05f) : MathUtils.lerp(this.velocity.x, 0, 0.2f);
		velocity.y = isFlying ? MathUtils.lerp(this.velocity.y, 0, 0.1f) : MathUtils.lerp(this.velocity.y, 0, 0.01f);
		velocity.z = isFlying ? MathUtils.lerp(this.velocity.z, 0, 0.05f) : MathUtils.lerp(this.velocity.z, 0, 0.2f);

		if(velocity.dst(Vector3.Zero) < 0.001) velocity.set(Vector3.Zero);

		this.isWalking = (velocity.x != 0 || velocity.z != 0);
	}

	final Vector3 add = new Vector3(), direction = new Vector3(), noam = new Vector3(), temp = new Vector3();

	public void process(Camera camera, FirstPersonCameraController cameraController) {
		float y = this.isFlying ? 0 : -0.01f;
		float speed = 0.015f;

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			if (this.onGround) {
				y = 0.15f;
			} else if (this.isFlying) {
				y = 0.02f;
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && this.isFlying) {
			y = -0.02f;
		}

		noam.set(camera.direction).nor();
		float angle = MathUtils.atan2(noam.x, noam.z);

//        player.instance.nodes.first().rotation.set(Vector3.Y,angle);
//        player.instance.calculateTransforms();

		direction.set(MathUtils.sin(angle), 0, MathUtils.cos(angle));
		add.setZero();

		temp.set(direction);

		if (Gdx.input.isKeyPressed(Input.Keys.W))
			add.add(temp
					.scl(speed * (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? (this.isFlying ? 2f : 1.5f) : (this.isFlying ? 0.75f : 1f))));
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			add.add(temp.scl(-speed  * (this.isFlying ? 0.75f : 1f)));

		temp.set(direction.rotate(Vector3.Y, -90));

		if (Gdx.input.isKeyPressed(Input.Keys.A))
			add.add(temp
					.scl(-speed * (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? (this.isFlying ? 0.5f : 1f) : (this.isFlying ? 0.75f : 1f))));
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			add.add(temp
					.scl(speed * (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? (this.isFlying ? 0.5f : 1f) :(this.isFlying ? 0.75f : 1f))));

		if (!add.equals(Vector3.Zero) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
				&& Gdx.input.isKeyPressed(Input.Keys.W))
			cameraController.targetFOV = 90; // changed from 110 to 90
		else
			cameraController.targetFOV = 80; // changed from 90 to 80

		add.y = y;
		this.applyForce(add);
	}

	private final CollisionBox bintersector = new CollisionBox();
	private final Array<CollisionBox> boxes = new Array<>();
	private final BlockPos blockPos = new BlockPos();

	public void move(World world, float xMove, float yMove, float zMove) {

		collisionBox.expand(xMove, yMove, zMove, bintersector);

		final int xMin = MathUtils.floor(bintersector.xMin);
		final int yMin = MathUtils.floor(bintersector.yMin);
		final int zMin = MathUtils.floor(bintersector.zMin);

		final int xMax = MathUtils.floor(bintersector.xMax)+1;
		final int yMax = MathUtils.floor(bintersector.yMax)+1;
		final int zMax = MathUtils.floor(bintersector.zMax)+1;
		
		for (int x = xMin; x < xMax; x++)
		for (int y = yMin; y < yMax; y++)
		for (int z = zMin; z < zMax; z++) {
			Block block = world.getBlock(blockPos.set(x, y, z));
			block.addCollisions(blockPos, boxes, CollisionBox.POOL);
			if (!block.isAir()) block.onEntityCollide(blockPos);
		}

		final float xLastMove = xMove;
		final float yLastMove = yMove;
		final float zLastMove = zMove;

		final int size = boxes.size;
		for (int i = 0; i < size; i++) {
			yMove = boxes.get(i).collideY(collisionBox, yMove);
		}
		collisionBox.yMin += yMove;
		collisionBox.yMax += yMove;
		
		onGround = (yLastMove != yMove) && yLastMove < 0f;
		
		for (int i = 0; i < size; i++) {
			final float lastX = xMove;
			xMove = boxes.get(i).collideX(collisionBox, xMove);
			if (xMove != lastX && onGround) { // check if collide and on ground.
				final float step = boxes.get(i).yMax - collisionBox.yMin;
				if (step <= 0.5f) { // check if 0.5 step distant.
					collisionCheck.set(collisionBox);
					collisionCheck.yMin += step;
					collisionCheck.yMax += step;
					collisionCheck.xMin += lastX;
					collisionCheck.xMax += lastX;
					if (checkValidSpot(world)) { // check if it valid spot to teleport.
						collisionBox.yMin += step;
						collisionBox.yMax += step;
						xMove = lastX;
					}
				}
			}
		}
		collisionBox.xMin += xMove;
		collisionBox.xMax += xMove;
		
		for (int i = 0; i < size; i++) {
			final float lastZ = zMove;
			zMove = boxes.get(i).collideZ(collisionBox, zMove);
			if (zMove != lastZ && onGround) { // check if collide and on ground.
				final float step = boxes.get(i).yMax - collisionBox.yMin;
				if (step <= 0.5f) { // check if 0.5 step distant.
					collisionCheck.set(collisionBox);
					collisionCheck.yMin += step;
					collisionCheck.yMax += step;
					collisionCheck.zMin += lastZ;
					collisionCheck.zMax += lastZ;
					if (checkValidSpot(world)) { // check if it valid spot to teleport.
						collisionBox.yMin += step;
						collisionBox.yMax += step;
						zMove = lastZ;
					}
				}
			}
			
		}
		collisionBox.zMin += zMove;
		collisionBox.zMax += zMove;
		
				
		if (xLastMove != xMove) {
			velocity.x = 0f;
		}
		if (yLastMove != yMove) {
			velocity.y = 0f;
		}
		if (zLastMove != zMove) {
			velocity.z = 0f;
		}

		position.x = (collisionBox.xMin + collisionBox.xMax)*0.5f;
		position.y = collisionBox.yMin;
		position.z = (collisionBox.zMin + collisionBox.zMax)*0.5f;

		CollisionBox.POOL.freeAll(boxes);
		boxes.size = 0;
	}
	
	private final CollisionBox collisionCheck = new CollisionBox();
	private final CollisionBox collisionTemp = new CollisionBox();
	private boolean checkValidSpot(World world) {
		final int xMin = MathUtils.floor(collisionCheck.xMin);
		final int yMin = MathUtils.floor(collisionCheck.yMin);
		final int zMin = MathUtils.floor(collisionCheck.zMin);
		final int xMax = MathUtils.floor(collisionCheck.xMax)+1;
		final int yMax = MathUtils.floor(collisionCheck.yMax)+1;
		final int zMax = MathUtils.floor(collisionCheck.zMax)+1;
		
		for (int x = xMin; x < xMax; x++)
		for (int y = yMin; y < yMax; y++)
		for (int z = zMin; z < zMax; z++) {
			final Block block = world.getBlock(blockPos.set(x, y, z));
			if (!block.getMaterial().hasCollision()) continue;
			
			final Array<BoundingBox> boxes = block.getBoundingBoxes(blockPos);
			for (BoundingBox box : boxes) {
				if (collisionTemp.set(box).move(blockPos.x, blockPos.y, blockPos.z).intersects(collisionCheck)) {
					return false;
				}
			}
		}
		
		return true;
	}
}
