package com.travall.game.blocks.data;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.math.MathUtils;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Utils;

public abstract class DataComponent {
	protected final int size;
	
	private int offset;
	private int dataAnd;
	private int dataInv;
	
	/** @param bitSets true for bits size allocation. Else false for number size for allocation.  */
	public DataComponent(int size, boolean bitSets) {
		if (bitSets) {
			this.size = size;
		} else {
			switch (MathUtils.nextPowerOfTwo(size)) {
			case 1<<0:  this.size = 1; break;
			case 1<<1:  this.size = 2; break;
			case 1<<2:  this.size = 3; break;
			case 1<<3:  this.size = 4; break;
			case 1<<4:  this.size = 5; break;
			case 1<<5:  this.size = 6; break;
			case 1<<6:  this.size = 7; break;
			case 1<<7:  this.size = 8; break;
			case 1<<8:  this.size = 9; break;
			case 1<<9:  this.size = 10; break;
			case 1<<10: this.size = 11; break;
			case 1<<11: this.size = 12; break;
			case 1<<12: this.size = 13; break;
			case 1<<13: this.size = 14; break;
			case 1<<14: this.size = 15; break;
			case 1<<15: this.size = 16; break;
			default: throw new IllegalArgumentException();
			}
		}
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
	
	public abstract String getDefaultKey();
}
