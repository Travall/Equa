package com.travall.game.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.StreamUtils;
import com.travall.game.utils.Logger;
import com.travall.game.utils.Lzma;
import com.travall.game.utils.Properties;
import com.travall.game.world.World;

public final class WorldIO {
	
	private static final Logger LOG = new Logger(WorldIO.class);
	
	static final int[] OFFSET = {24, 16, 8, 0};
	static final int[] AND = {~(255<<24), ~(255<<16), ~(255<<8), ~255};
	
	static final int SIZE = World.mapSize * World.mapHeight * World.mapSize * Integer.BYTES;
	
	public static final String DIR = "saves/";
	public static final String WORLD_DAT = "world.dat";
	public static final String PROPS_DAT = "props.json";
	
	static final int BUFFER_SIZE = 1<<13;
	private static final byte[] STREAM_BUFFER = new byte[BUFFER_SIZE];
	static final byte[] BUFFER = new byte[BUFFER_SIZE];
	static final Deflater DEFLATER = new Deflater(1); // The compression level (0-9).
	static final Inflater INFLATER = new Inflater();
	
	private static final Json JSON = new Json();
	private static final Packet PACKET = new Packet();

	public static void save(World world, Properties props) throws Exception  {
		OutputStream output = null;
		InputStream input = null;
		final FileHandle folder = world.folder;
		
		try {
			output = new DeflaterOutputStream(folder.child(WORLD_DAT).write(false));
			input = new WorldInputStream();
			StreamUtils.copyStream(input, output, STREAM_BUFFER);
			JSON.toJson(props, Properties.class, folder.child(PROPS_DAT));
			//Lzma.compress(input, output);
		} finally {
			StreamUtils.closeQuietly(output);
		}
		LOG.info("Saving done!");
	}

	public static Packet load(FileHandle folder) throws Exception {
		InputStream input = null;
		OutputStream output = null;
		PACKET.isLoad = true;
		
		try {
			input = new InflaterInputStream(folder.child(WORLD_DAT).read());
			output = new WorldOutputStream();
			
			FileHandle propsFile = folder.child(PROPS_DAT);
			if (propsFile.exists()) {
				PACKET.props = JSON.fromJson(Properties.class, folder.child(PROPS_DAT));
			} else {
				PACKET.props = new Properties();
				LOG.info("No " + PROPS_DAT + " file detected.");
			}
			
			PACKET.world = new World(folder);
			copyStream(input, output);
			//Lzma.decompress(input, output);
		} finally {
			StreamUtils.closeQuietly(input);
		}
		LOG.info("Loading done!");
		
		return PACKET;
	}
	
	private static void copyStream(InputStream input, OutputStream output) throws IOException {
		int bytesRead, size = 0;
		while ((bytesRead = input.read(STREAM_BUFFER, size, BUFFER_SIZE - size)) != -1) {
			size += bytesRead;
			if (size != BUFFER_SIZE) {
				continue;
			}
			output.write(STREAM_BUFFER, 0, size);
			size = 0;
		}
	}
	
	public static FileHandle getFolder(String folder) {
		return FileIO.external(DIR + folder);
	}
	
	public final static class Packet {
		public World world;
		public Properties props;
		public boolean isLoad;
	}
}
