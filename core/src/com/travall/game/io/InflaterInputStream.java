package com.travall.game.io;

import java.io.IOException;
import java.io.InputStream;

class InflaterInputStream extends java.util.zip.InflaterInputStream {

	public InflaterInputStream(InputStream in) {
		super(in, WorldIO.INFLATER, 1);
		this.buf = WorldIO.BUFFER;
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			inf.reset();
		}
	}
}
