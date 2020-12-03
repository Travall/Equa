package com.travall.game.blocks.data;

import com.travall.game.utils.BlockPos;

public final class TypeComponent extends DataComponent {
	
	public static final String KEY = "type";
	
	public TypeComponent(int size) {
		super(size, false);
	}
	
	public int getType(BlockPos pos) {
		return getData(pos);
	}
	
	public void setType(BlockPos pos, int type) {
		setData(pos, type);
	}

	@Override
	public String getKey() {
		return KEY;
	}
}
