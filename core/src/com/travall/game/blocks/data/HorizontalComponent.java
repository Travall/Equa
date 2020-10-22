package com.travall.game.blocks.data;

import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class HorizontalComponent extends DataComponent {
	
	public static final String KEY = "horizontal";

	public HorizontalComponent() {
		super(2, true);
	}
	
	public Facing getFace(BlockPos pos) {
		switch (getData(pos)) {
		case 0: return Facing.NORTH;
		case 1: return Facing.EAST;
		case 2: return Facing.SOUTH;
		case 3: return Facing.WEST;
		default: throw new IllegalArgumentException("Invailed data: " + getData(pos));
		}
	}
	
	public void setFace(BlockPos pos, Facing face) {
		switch (face) {
		case NORTH: setData(pos, 0); return;
		case EAST:  setData(pos, 1); return;
		case SOUTH: setData(pos, 2); return;
		case WEST:  setData(pos, 3); return;
		default: throw new IllegalArgumentException("Invailed face: " + face);
		}
	}

	@Override
	public String getKey() {
		return KEY;
	}
}
