package com.travall.game.blocks.data;

import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class FaceComponent extends DataComponent {

	public FaceComponent() {
		super(3); // 3 is the required bits to allocate.
	}
	
	public Facing getFace(BlockPos pos) {
		switch (getData(pos)) {
		case 0: return Facing.UP;
		case 1: return Facing.DOWN;
		case 2: return Facing.NORTH;
		case 3: return Facing.EAST;
		case 4: return Facing.SOUTH;
		case 5: return Facing.WEST;
		default: return null;
		}
	}
	
	public void setFace(BlockPos pos, Facing face) {
		switch (face) {
		case UP: setData(pos, 0); return;
		case DOWN: setData(pos, 1); return;
		case NORTH: setData(pos, 2); return;
		case EAST: setData(pos, 3); return;
		case SOUTH: setData(pos, 4); return;
		case WEST: setData(pos, 5); return;
		}
	}
}
