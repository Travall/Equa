package com.travall.game.io;

import static com.travall.game.world.World.STATIC_DATA;

import java.io.OutputStream;

public class WorldOutputStream extends OutputStream {
	private int pos;

	@Override
	public void write(int b) {
		if (pos >= WorldIO.SIZE) return;
		
		final int byteIndex = pos & 3;
		final int intIndex  = pos >>> 2;
		pos++;
		
		final int x, y, z;
		/*
		x = intIndex % mapSize;
		y = (intIndex / mapSize) % mapHeight;
		z = (intIndex / mapHeight / mapSize) % mapSize;
		*/
		x = intIndex & 511;
		y = (intIndex >>> 9) & 255;
		z = (intIndex >>> 8 >>> 9) & 511;

		STATIC_DATA[x][y][z] = (STATIC_DATA[x][y][z] & WorldIO.AND[byteIndex]) | (b << WorldIO.OFFSET[byteIndex]);
	}
	
	public void reset() {
		pos = 0;
	}
}
