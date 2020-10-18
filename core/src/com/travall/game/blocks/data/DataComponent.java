package com.travall.game.blocks.data;

import static com.travall.game.world.World.world;

import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Utils;

public class DataComponent {
	protected final int size;
	
	private int offset;
	private int dataAnd;
	private int dataInv;
	
	public DataComponent(int bitSize) {
		size = bitSize;
	}
	
	final void genData(int offset) {
		this.offset = offset;
		dataAnd = Utils.createANDbits(size)<<offset;
		dataInv = ~dataAnd;
	}
	
	protected final int getData(BlockPos pos) {
		return (world.data[pos.x][pos.y][pos.z] & dataAnd) >>> offset;
	}
	
	protected final void setData(BlockPos pos, int data) {
		world.data[pos.x][pos.y][pos.z] = (world.data[pos.x][pos.y][pos.z] & dataInv) | (data << offset);
	}
}
