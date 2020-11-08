package com.travall.game.utils;

import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.utils.compression.lzma.Decoder;
import com.badlogic.gdx.utils.compression.lzma.Encoder;

public final class Lzma {
	
	private Lzma() {}

	// Recommended setting for speed.
	private static final int DICTIONARY_SIZE = 1 << 17;
	private static final int NFB = 16;

	public static void compress(InputStream in, OutputStream out) throws Exception {
		final Encoder encoder = new Encoder();

		if (!encoder.SetDictionarySize(DICTIONARY_SIZE)) throw new RuntimeException("Incorrect dictionary size");
		if (!encoder.SetNumFastBytes(NFB)) throw new RuntimeException("Incorrect -fb value");

		encoder.SetMatchFinder(1);
		encoder.SetLcLpPb(3, 0, 2);
		encoder.WriteCoderProperties(out);

		long fileSize;
		if ((fileSize = in.available()) == 0) {
			fileSize = -1;
		}
		encoder.SetEndMarkerMode(fileSize == -1);

		for (int i = 0; i < 8; i++) {
			out.write((int) (fileSize >>> (8 * i)) & 0xFF);
		}

		encoder.Code(in, out, -1, -1, null);
	}

	public static void decompress(InputStream in, OutputStream out) throws Exception {
		final int propertiesSize = 5;
		final byte[] properties = new byte[propertiesSize];
		if (in.read(properties, 0, propertiesSize) != propertiesSize)
			throw new RuntimeException("input .lzma file is too short");

		final Decoder decoder = new Decoder();
		if (!decoder.SetDecoderProperties(properties))
			throw new RuntimeException("Incorrect stream properties");

		long outSize = 0;
		for (int i = 0; i < 8; i++) {
			int v = in.read();
			if (v < 0) {
				throw new RuntimeException("Can't read stream size");
			}
			outSize |= ((long) v) << (8 * i);
		}

		if (!decoder.Code(in, out, outSize)) {
			throw new RuntimeException("Error in data stream");
		}
	}
}
