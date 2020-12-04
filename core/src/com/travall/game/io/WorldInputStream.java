package com.travall.game.io;

import static com.travall.game.world.World.STATIC_DATA;

import java.io.InputStream;

class WorldInputStream extends InputStream {
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
	public int read(final byte[] b, final int off, final int len) {
		if (pos >= WorldIO.SIZE) return -1;
		
        int p = pos, i = 0;
        for (; i < len; i += 4) {
        	final int intIndex = (p + i) >>> 2;
        	final int x, y, z;
    		x = intIndex & 511;
    		y = (intIndex >>> 9) & 255;
    		z = (intIndex >>> 8 >>> 9) & 511;
    		final int data = STATIC_DATA[x][y][z];
    		
    		b[i]   = (byte) (data >>> 24);
    		b[i+1] = (byte) ((data >>> 16) & 0xFF);
    		b[i+2] = (byte) ((data >>> 8) & 0xFF);
    		b[i+3] = (byte) (data & 0xFF);
		}
        pos += i;
        return i;
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
