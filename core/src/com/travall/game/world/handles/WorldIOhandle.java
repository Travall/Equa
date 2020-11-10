package com.travall.game.world.handles;

import com.badlogic.gdx.files.FileHandle;
import com.travall.game.io.WorldIO;
import com.travall.game.utils.AsyncThreaded;
import com.travall.game.world.World;
import com.travall.game.world.gen.DefaultGen;
import com.travall.game.world.gen.Generator;

public class WorldIOhandle extends AsyncThreaded<World> {

	public final Generator generator = new DefaultGen();
	
	private volatile FileHandle folder;
	private volatile World world;
	private volatile boolean isSaving;
	
	private boolean exists;
	
	public WorldIOhandle() {
		super("World Loader");
	}
	
	public void load(FileHandle folder) {
		this.folder = folder;
		this.exists = folder.exists();
		this.isSaving = false;
		result = exe.submit(this);
	}
	
	public void save(World world) {
		this.world = world;
		this.isSaving = true;
		result = exe.submit(this);
	}

	@Override
	public World call() throws Exception {
		if (isSaving) {
			try {
				WorldIO.save(world);
			} finally {
				world = null;
			}
			return null;
		}
		
		if (folder.exists()) {
			final World world = WorldIO.load(folder);
			world.createShadowMap(false);
			return world;
		}
		
		return new World(folder, generator);
	}
	
	public String getStatus() {
		if (isSaving) return "Saving..";
		return exists ? "Loading.." : generator.getStatus();
	}
}
