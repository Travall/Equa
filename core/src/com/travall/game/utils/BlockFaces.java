package com.travall.game.utils;

public class BlockFaces {
	public Facing up    = Facing.UP;
	public Facing down  = Facing.DOWN;
	public Facing north = Facing.NORTH;
	public Facing east  = Facing.EAST;
	public Facing south = Facing.SOUTH;
	public Facing west  = Facing.WEST;
	
	public void rotateRight(BlockFaces out) {
		out.north = north.rotateRight();
		out.east  = east.rotateRight();
		out.south = south.rotateRight();
		out.west  = west.rotateRight();
	}
	
	public void rotateLeft(BlockFaces out) {
		out.north = north.rotateLeft();
		out.east  = east.rotateRight();
		out.south = south.rotateRight();
		out.west  = west.rotateRight();
	}
	
	public void rotate(BlockFaces out, int num) {
		out.north = north.rotate(num);
		out.east  = east.rotate(num);
		out.south = south.rotate(num);
		out.west  = west.rotate(num);
	}
}
