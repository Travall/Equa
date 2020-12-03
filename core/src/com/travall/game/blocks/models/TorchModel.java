package com.travall.game.blocks.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.Torch;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class TorchModel implements IBlockModel {

	private final TextureRegion texture;

	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;
	private final QuadNode node1, node2, node3, node4, node5, node6;
	private final Torch torch;

	public TorchModel(Torch torch, BlockTextures textures) {
		this.texture = textures.north;
		this.torch = torch;

		float width = 2/16f;
		float height = 10/16f;
		float length = 2/16f;

		quad1 = new QuadNode();
		quad1.p1.set(1, 1, 0);
		quad1.p2.set(0, 1, 0);
		quad1.p3.set(0, 1, 1);
		quad1.p4.set(1, 1, 1);
		quad1.face = Facing.UP;
		quad1.simpleLight = true;
		quad1.region.setRegion(textures.top);

		quad2 = new QuadNode();
		quad2.p1.set(0, 0, 0);
		quad2.p2.set(1, 0, 0);
		quad2.p3.set(1, 0, 1);
		quad2.p4.set(0, 0, 1);
		quad2.face = Facing.DOWN;
		quad2.simpleLight = true;
		quad2.region.setRegion(textures.bottom);

		quad3 = new QuadNode();
		quad3.p1.set(1, 0, 1);
		quad3.p2.set(1, 1, 1);
		quad3.p3.set(0, 1, 1);
		quad3.p4.set(0, 0, 1);
		quad3.face = Facing.NORTH;
		quad3.simpleLight = true;
		quad3.region.setRegion(textures.south);

		quad4 = new QuadNode();
		quad4.p1.set(1, 0, 0);
		quad4.p2.set(1, 1, 0);
		quad4.p3.set(1, 1, 1);
		quad4.p4.set(1, 0, 1);
		quad4.face = Facing.EAST;
		quad4.simpleLight = true;
		quad4.region.setRegion(textures.south);

		quad5 = new QuadNode();
		quad5.p1.set(0, 0, 0);
		quad5.p2.set(0, 1, 0);
		quad5.p3.set(1, 1, 0);
		quad5.p4.set(1, 0, 0);
		quad5.face = Facing.SOUTH;
		quad5.simpleLight = true;
		quad5.region.setRegion(textures.south);

		quad6 = new QuadNode();
		quad6.p1.set(0, 0, 1);
		quad6.p2.set(0, 1, 1);
		quad6.p3.set(0, 1, 0);
		quad6.p4.set(0, 0, 0);
		quad6.face = Facing.WEST;
		quad6.simpleLight = true;
		quad6.region.setRegion(textures.west);
		
		quad1.scl(1,height,1);
		quad3.scl(1,1,8/16f + (width / 2));
		quad4.scl(8/16f + (width / 2),1,1);
		quad5.add(0,0,8/16f - (length / 2));
		quad6.add(8/16f - (length / 2),0,0);
		
		node1 = new QuadNode().set(quad1);
		node2 = new QuadNode().set(quad2);
		node3 = new QuadNode().set(quad3);
		node4 = new QuadNode().set(quad4);
		node5 = new QuadNode().set(quad5);
		node6 = new QuadNode().set(quad6);
	}

	@Override
	public void build(QuadBuilder builder, BlockPos position) {
		final Facing face = torch.face.getFace(position);
		final Matrix4 matrix; 
		switch (face) {
			case NORTH: matrix = MUL_NORTH; break;
			case EAST: matrix = MUL_EAST; break;
			case SOUTH: matrix = MUL_SOUTH; break;
			case WEST: matrix = MUL_WEST; break;
			default: matrix = NONE; break;
		}
		
		node1.setPos(quad1).mul(matrix);
		node2.setPos(quad2).mul(matrix);
		node3.setPos(quad3).mul(matrix);
		node4.setPos(quad4).mul(matrix);
		node5.setPos(quad5).mul(matrix);
		node6.setPos(quad6).mul(matrix);
		
		node1.rect(builder, position);
		node2.rect(builder, position);
		node3.rect(builder, position);
		node4.rect(builder, position);
		node5.rect(builder, position);
		node6.rect(builder, position);
	}

	@Override
	public TextureRegion getDefaultTexture(BlockPos pos, int data) {
		return texture;
	}
	
	private static final Matrix4 NONE = new Matrix4();
	private static final Matrix4 MUL_SOUTH = new Matrix4().setFromEulerAngles(180f, -20f, 0).trn(0f, 0.2f, -0.32f);
	private static final Matrix4 MUL_WEST  = new Matrix4().setFromEulerAngles(90f, -20f, 0).trn(0.32f, 0.2f, 0f);
	private static final Matrix4 MUL_NORTH = new Matrix4().setFromEulerAngles(0, -20f, 0).trn(0f, 0.2f, 0.3f);
	private static final Matrix4 MUL_EAST  = new Matrix4().setFromEulerAngles(270f, -20f, 0).trn(-0.32f, 0.2f, 0f);
	
	private static final Array<BoundingBox> UP    = new Array<>(1);
	private static final Array<BoundingBox> NORTH = new Array<>(1);
	private static final Array<BoundingBox> EAST  = new Array<>(1);
	private static final Array<BoundingBox> SOUTH = new Array<>(1);
	private static final Array<BoundingBox> WEST  = new Array<>(1);
	
	static {
		BoundingBox box = new BoundingBox(MIN.set(7/16f,0,7/16f), MAX.set(9/16f, 10/16f, 9/16f));
		BoundingBox tmp;
		UP.add(new BoundingBox(box));
		
		box.max.sub(0.5f, 0.5f, 0.5f);
		box.min.sub(0.5f, 0.5f, 0.5f);
		
		tmp = new BoundingBox(box).mul(MUL_NORTH);
		tmp.max.add(0.5f, 0.5f, 0.5f);
		tmp.min.add(0.5f, 0.5f, 0.5f);
		NORTH.add(tmp);
		
		tmp = new BoundingBox(box).mul(MUL_EAST);
		tmp.max.add(0.5f, 0.5f, 0.5f);
		tmp.min.add(0.5f, 0.5f, 0.5f);
		EAST.add(tmp);
		
		tmp = new BoundingBox(box).mul(MUL_SOUTH);
		tmp.max.add(0.5f, 0.5f, 0.5f);
		tmp.min.add(0.5f, 0.5f, 0.5f);
		SOUTH.add(tmp);
		
		tmp = new BoundingBox(box).mul(MUL_WEST);
		tmp.max.add(0.5f, 0.5f, 0.5f);
		tmp.min.add(0.5f, 0.5f, 0.5f);
		WEST.add(tmp);
	}
	
	@Override
	public Array<BoundingBox> getBoundingBoxes(BlockPos pos) {
		switch (torch.face.getFace(pos)) {
		case NORTH: return NORTH;
		case EAST:  return EAST;
		case SOUTH: return SOUTH;
		case WEST:  return WEST;
		default: return UP;
	}
	}
}
