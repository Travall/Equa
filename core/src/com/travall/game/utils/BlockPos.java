package com.travall.game.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public final class BlockPos {
	private static final Logger LOG = new Logger(BlockPos.class);
	
	/** Max buffer size. */
	private static final int MAX_BUFFER_SIZE = 1<<12;
	/** BlockPos buffer. */
	private static final BlockPos[] TABLE = new BlockPos[MAX_BUFFER_SIZE];
	
	/** Position of the current index. */
	private static int position = 0;
	
	/** Create a unsafe BlockPos. */
	public static BlockPos newBlockPos() {
		if (position >= MAX_BUFFER_SIZE) return new BlockPos();
		BlockPos blockPos = TABLE[position];
		blockPos = blockPos == null ? TABLE[position] = new BlockPos().setUnsafe() : blockPos;
		++position;
		return blockPos;
	}
	
	public static void reset() {
		if (position >= MAX_BUFFER_SIZE) LOG.info("All pool objects has been used and was creating new objects."); // Performances warning
		position = 0;
	}
	
	public int x, y, z;
	private boolean isUnsafe;

	public BlockPos() {
	}

	public BlockPos(int x, int y, int z) {
		set(x, y, z);
	}

	public BlockPos(BlockPos pos) {
		set(pos);
	}
	
	public BlockPos(Vector3 pos) {
		set(pos);
	}
	
	public BlockPos set(Vector3 pos) {
		this.x = MathUtils.floor(x);
		this.y = MathUtils.floor(y);
		this.z = MathUtils.floor(z);
		return this;
	}
	
	public BlockPos set(BlockPos pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
		return this;
	}
	
	public BlockPos set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public BlockPos setZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}
	
	public BlockPos offset(Facing face) {
		return offset(face.offset);
	}
	
	public BlockPos offset(Facing face, int num) {
		final BlockPos offset = face.offset;
		return offset(offset.x*num, offset.y*num, offset.z*num);
	}

	public BlockPos offset(BlockPos pos) {
		return offset(pos.x, pos.y, pos.z);
	}

	/** Create a offset BlockPos from BlockPos's pool. Use blockPos.copy() to store the BlockPos. */
	public BlockPos offset(int x, int y, int z) {
		return newBlockPos().set(this.x + x, this.y + y, this.z + z);
	}
	
	public BlockPos add(BlockPos pos) {
		return add(pos.x, pos.y, pos.z);
	}

	public BlockPos add(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public BlockPos sub(BlockPos pos) {
		return sub(pos.x, pos.y, pos.z);
	}

	public BlockPos sub(int x, int y, int z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	public BlockPos negate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	
	private BlockPos setUnsafe() {
		isUnsafe = true;
		return this;
	}
	
	public boolean isUnsafe() {
		return isUnsafe;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (null == obj) return false;
		if (obj.getClass() == BlockPos.class) {
			final BlockPos p = (BlockPos)obj;
			return p.x == x && p.y == y && p.z == z;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "("+x+", "+y+", "+x+")";
	}
	
	public BlockPos copy() {
		return new BlockPos(this);
	}
}
