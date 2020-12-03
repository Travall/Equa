package com.travall.game.blocks.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.Door;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class DoorModel implements IBlockModel {
	
	private final TextureRegion upper = UltimateTexture.createRegion(0, 4);
	private final TextureRegion lower = UltimateTexture.createRegion(0, 5);
	
	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;
	private final QuadNode node1, node2, node3, node4, node5, node6;
	
	private final Door door;
	
	private static final float len = 2f/16f;
	
	public DoorModel(Door door) {
		this.door = door;
		
		quad1 = new QuadNode();
		quad1.p1.set(1, 1, 0);
		quad1.p2.set(0, 1, 0);
		quad1.p3.set(0, 1, len);
		quad1.p4.set(1, 1, len);
		quad1.region.setTexture(UltimateTexture.texture);
		quad1.face = Facing.UP;
		
		quad2 = new QuadNode();
		quad2.p1.set(0, 0, 0);
		quad2.p2.set(1, 0, 0);
		quad2.p3.set(1, 0, len);
		quad2.p4.set(0, 0, len);
		quad2.region.setTexture(UltimateTexture.texture);
		quad2.face = Facing.DOWN;
		
		quad3 = new QuadNode();
		quad3.p1.set(0, 0, 0);
		quad3.p2.set(0, 1, 0);
		quad3.p3.set(1, 1, 0);
		quad3.p4.set(1, 0, 0);
		quad3.region.setTexture(UltimateTexture.texture);
		quad3.face = Facing.NORTH;
		
		quad4 = new QuadNode();
		quad4.p1.set(1, 0, 0);
		quad4.p2.set(1, 1, 0);
		quad4.p3.set(1, 1, len);
		quad4.p4.set(1, 0, len);
		quad4.region.setTexture(UltimateTexture.texture);
		quad4.face = Facing.EAST;
		
		quad5 = new QuadNode();
		quad5.p1.set(1, 0, len);
		quad5.p2.set(1, 1, len);
		quad5.p3.set(0, 1, len);
		quad5.p4.set(0, 0, len);
		quad5.region.setTexture(UltimateTexture.texture);
		quad5.face = Facing.SOUTH;
		
		quad6 = new QuadNode();
		quad6.p1.set(0, 0, len);
		quad6.p2.set(0, 1, len);
		quad6.p3.set(0, 1, 0);
		quad6.p4.set(0, 0, 0);
		quad6.region.setTexture(UltimateTexture.texture);
		quad6.face = Facing.WEST;
		
		node1 = new QuadNode().set(quad1);
		node2 = new QuadNode().set(quad2);
		node3 = new QuadNode().set(quad3);
		node4 = new QuadNode().set(quad4);
		node5 = new QuadNode().set(quad5);
		node6 = new QuadNode().set(quad6);
	}

	@Override
	public void build(QuadBuilder builder, BlockPos position) {
		final boolean isUpper = door.isUpper(position);
		setTexture(isUpper ? upper : lower);
		
		final boolean isOpen = door.isOpen(position);
		Matrix4 matrix = isOpen ? OPEN : NONE;
		node1.setPos(quad1).mul(matrix);
		node2.setPos(quad2).mul(matrix);
		node3.setPos(quad3).mul(matrix);
		node4.setPos(quad4).mul(matrix);
		node5.setPos(quad5).mul(matrix);
		node6.setPos(quad6).mul(matrix);
		
		final Facing face = door.horizontalComponent.getFace(position);
		switch (face) {
		case EAST:  matrix = MUL_EAST; break;
		case SOUTH: matrix = MUL_SOUTH; break;
		case WEST:  matrix = MUL_WEST; break;
		default: matrix = NONE; break;
		}
		
		node1.mul(matrix);
		node2.mul(matrix);
		node3.mul(matrix);
		node4.mul(matrix);
		node5.mul(matrix);
		node6.mul(matrix);
		
		if (isUpper) {
			node1.rect(builder, position);
		} else {
			node2.rect(builder, position);
		}
		
		node3.face = isOpen ? quad3.face.rotateLeft() : quad3.face;
		node3.face = node3.face.rotate(face.getRotateValue());
		
		
		node4.face = isOpen ? quad4.face.rotateLeft() : quad4.face;
		node4.face = node4.face.rotate(face.getRotateValue());
		
		
		node5.face = isOpen ? quad5.face.rotateLeft() : quad5.face;
		node5.face = node5.face.rotate(face.getRotateValue());
		
		
		node6.face = isOpen ? quad6.face.rotateLeft() : quad6.face;
		node6.face = node6.face.rotate(face.getRotateValue());
		
		node5.isInside = !isOpen;
		node3.isInside = isOpen;
		
		node3.rect(builder, position);
		node4.rect(builder, position);
		node5.rect(builder, position);
		node6.rect(builder, position);
	}
	
	private void setTexture(TextureRegion region) {
		node1.region.setRegion(region, 15, 0, 1, 16);
		node2.region.setRegion(region, 15, 0, 1, 16);
		node3.region.setRegion(region);
		
		node4.region.setRegion(region);
		node4.region.setRegionWidth(1);
		
		node5.region.setRegion(region);
		node5.region.flip(true, false);
		
		node6.region.setRegion(region, 15, 0, 1, 16);
	}
	
	public static final Matrix4 NONE = new Matrix4();
	public static final Matrix4 OPEN = new Matrix4().setFromEulerAngles(90f, 0, 0).trn(1f-len, 0f, 0f);
	
	private static final Matrix4 MUL_EAST  = new Matrix4().setFromEulerAngles(270f, 0, 0);
	private static final Matrix4 MUL_SOUTH = new Matrix4().setFromEulerAngles(180f, 0, 0);
	private static final Matrix4 MUL_WEST  = new Matrix4().setFromEulerAngles(90f, 0, 0);
	
	
	public static final BoundingBox NORTH;;
	public static final Array<BoundingBox> TEMP = new Array<>(1);
	
	static {
		NORTH = new BoundingBox(MIN.set(0, 0, 0), MAX.set(1, 1, 2f/16f));
		TEMP.add(new BoundingBox());
	}
	
	@Override
	public Array<BoundingBox> getBoundingBoxes(BlockPos pos) {
		BoundingBox tmp = TEMP.get(0).set(NORTH);
		final boolean isOpen = door.isOpen(pos);
		tmp.min.sub(0.5f);
		tmp.max.sub(0.5f);
		tmp.mul(isOpen ? OPEN : NONE);
		final Matrix4 matrix;
		switch (door.horizontalComponent.getFace(pos)) {
		case EAST:  matrix = MUL_EAST; break;
		case SOUTH: matrix = MUL_SOUTH; break;
		case WEST:  matrix = MUL_WEST; break;
		default: matrix = NONE; break;
		}
		tmp.mul(matrix);
		tmp.min.add(0.5f);
		tmp.max.add(0.5f);
		return TEMP;
	}

	@Override
	public TextureRegion getDefaultTexture(BlockPos pos, int data) {
		return upper;
	}
}
