package com.travall.game.io;

import java.io.IOException;
import java.io.OutputStream;

public class DeflaterOutputStream extends java.util.zip.DeflaterOutputStream {
	
	private final byte[] bufTmp;

	public DeflaterOutputStream(OutputStream out) {
		super(out, WorldIO.DEFLATER, 1);
		this.bufTmp = buf;
		this.buf = WorldIO.BUFFER;
	}
	
	@Override
	public void write(int b) throws IOException {
        bufTmp[0] = (byte)(b & 0xff);
        write(bufTmp, 0, 1);
    }
	
	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			def.reset();
		}
	}
}
