package com.travall.game.io;

import static com.travall.game.world.World.STATIC_DATA;

import java.io.OutputStream;

public class WorldOutputStream extends OutputStream {
	private int pos;

	@Override
	public void write(final int b) {
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
	
	@Override
	public void write(final byte b[], final int off, final int len) {
		if (pos >= WorldIO.SIZE) return;
		
		final int p = pos;
		int i = 0;
        for (; i < len; i += 4) {
        	final int intIndex = (p + i) >>> 2;
        	final int x, y, z;
    		x = intIndex & 511;
    		y = (intIndex >>> 9) & 255;
    		z = (intIndex >>> 8 >>> 9) & 511;
    		
    		STATIC_DATA[x][y][z] = (b[i] << 24) | (b[i+1] << 16) | (b[i+2] << 8) | b[i+3];
        }
        pos += i;
    }
	
	public void reset() {
		pos = 0;
	}
}
