package com.travall.game.blocks.data;

import com.travall.game.utils.BlockPos;
import com.travall.game.utils.LogTypes;

public class LogTypeComponent extends DataComponent {
	
	public static final String LOGTYPE = "logtype";

	public LogTypeComponent() {
		super(1, false);
	}
	
	public LogTypes getType(BlockPos pos) {
		switch (getData(pos)) {
		case 0: return LogTypes.OAK;
		case 1: return LogTypes.DARKOAK;
		default: return null;
		}
	}
	
	public void setType(BlockPos pos, LogTypes type) {
		switch (type) {
		case OAK: setData(pos, 0); return;
		case DARKOAK: setData(pos, 1); return;
		}
	}

	@Override
	public String getDefaultKey() {
		return LOGTYPE;
	}
}
