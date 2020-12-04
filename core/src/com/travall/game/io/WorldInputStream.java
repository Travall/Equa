package com.travall.game.io;

import static com.travall.game.world.World.STATIC_DATA;

import java.io.InputStream;

public class WorldInputStream extends InputStream {
	private int pos;

	@Override
	public int read() {
		if (pos >= WorldIO.SIZE) return -1;
		
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
		
		return (STATIC_DATA[x][y][z] >>> WorldIO.OFFSET[byteIndex]) & 0xFF;
	}
	
	@Override
	public int available() {
        return WorldIO.SIZE;
    }
	
	@Override
	public void reset() {
		pos = 0;
	}
}
