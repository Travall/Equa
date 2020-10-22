package com.travall.game.blocks.data;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.math.MathUtils;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Utils;

public abstract class DataComponent {
	/** The size of bits allocated. */
	protected final int size;
	
	private int offset;
	private int dataAnd;
	private int dataInv;
	
	/** @param bitSets true for bits size allocation. Else false for number size for allocation.  */
	protected DataComponent(int size, boolean bitSets) {
		if (bitSets) {
			this.size = size;
		} else {
			final int pow = MathUtils.nextPowerOfTwo(size);
			int result = 0;
			for (int i = 0; i < 16; i++) {
				if (pow == 1<<i) {
					result = i+1;
					break;
				}
			}
			if (result == 0) throw new IllegalArgumentException();
			this.size = result;
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
