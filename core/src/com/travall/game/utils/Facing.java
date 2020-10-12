package com.travall.game.utils;

public enum Facing {
	/** Facing Y+ */
	UP  (-1, Axis.Y, new BlockPos(0, 1, 0)),
	/** Facing Y- */
	DOWN(-1, Axis.Y,  new BlockPos(0, -1, 0)),
	/** Facing Z+ */
	NORTH(0, Axis.Z, new BlockPos(0, 0, 1)), 
	/** Facing X+ */
	EAST (1, Axis.X,  new BlockPos(1, 0, 0)),
	/** Facing Z- */
	SOUTH(2, Axis.Z, new BlockPos(0, 0, -1)),
	/** Facing X- */
	WEST (3, Axis.X,  new BlockPos(-1, 0, 0));
	
	private final int ID;
	
	public final Axis axis;
	public final BlockPos offset;
	
	private Facing(int ID, Axis axis, BlockPos offset) {
		this.ID = ID;
		this.offset = offset;
		this.axis = axis;
	}
	
	public Facing rotateRight() {
		switch (this) {
		case NORTH: return EAST;
		case EAST:  return SOUTH;
		case SOUTH: return WEST;
		case WEST:  return NORTH;
		default:    return this;
		}
	}
	
	public Facing rotateLeft() {
		switch (this) {
		case NORTH: return WEST;
		case EAST:  return NORTH;
		case SOUTH: return EAST;
		case WEST:  return SOUTH;
		default:    return this;
		}
	}
	
	public Facing rotate(int num) {
		if (ID == -1) return this;
		int newID = (ID+num)%4;
		newID += newID<0 ? 4 : 0;
		switch (newID) {
		case 0:  return NORTH;
		case 1:  return EAST;
		case 2:  return SOUTH;
		case 3:  return WEST;
		default: return this;
		}
	}
	
	public Facing invert() {
		switch (this) {
		case UP:    return DOWN;
		case DOWN:  return UP;
		case NORTH: return SOUTH;
		case EAST:  return WEST;
		case SOUTH: return NORTH;
		case WEST:  return EAST;
		default:    return this;
		}
	}
	
	public static enum Axis {
		X, Y, Z
	}
}
