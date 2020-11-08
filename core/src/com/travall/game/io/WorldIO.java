package com.travall.game.io;

import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.StreamUtils;
import com.travall.game.utils.Lzma;
import com.travall.game.world.World;

public final class WorldIO {
	
	static final int[] OFFSET = {24, 16, 8, 0};
	static final int[] AND = {~(255<<24), ~(255<<16), ~(255<<8), ~255};
	
	static final int SIZE = World.mapSize * World.mapHeight * World.mapSize * Integer.BYTES;
	
	public static final String DIR = "saves/";
	public static final String WORLD_DAT = "world.dat";

	public static void save(World world) throws Exception  {
		OutputStream output = null;
		InputStream input = null;
		
		try {
			output = world.folder.child(WORLD_DAT).write(false);
			input = new WorldInputStream();
			Lzma.compress(input, output);
		} finally {
			StreamUtils.closeQuietly(output);
		}
	}

	public static World load(FileHandle folder) throws Exception {
		InputStream input = null;
		OutputStream output = null;
		World world = null;
		
		try {
			input = folder.child(WORLD_DAT).read();
			output = new WorldOutputStream();
			world = new World(folder, null);
			Lzma.decompress(input, output);
		} finally {
			StreamUtils.closeQuietly(input);
		}
		
		return world;
	}
	
	public static FileHandle getFolder(String folder) {
		return FileIO.external(DIR + folder);
	}
}
