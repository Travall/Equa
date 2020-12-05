package com.travall.game.world.handles;

import com.badlogic.gdx.files.FileHandle;
import com.travall.game.io.WorldIO;
import com.travall.game.io.WorldIO.Packet;
import com.travall.game.utils.AsyncThreaded;
import com.travall.game.utils.Properties;
import com.travall.game.world.World;
import com.travall.game.world.gen.DefaultGen;
import com.travall.game.world.gen.Generator;

public class WorldIOhandle extends AsyncThreaded<Packet> {

	public final Generator generator = new DefaultGen();
	
	private final Packet packet = new Packet();
	
	private volatile FileHandle folder;
	private volatile World world;
	private volatile Properties props;
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
	
	public void save(World world, Properties props) {
		this.world = world;
		this.props = props;
		this.isSaving = true;
		result = exe.submit(this);
	}

	@Override
	public Packet call() throws Exception {
		if (isSaving) {
			try {
				WorldIO.save(world, props);
			} finally {
				world = null;
			}
			return null;
		}
		
		if (folder.exists()) {
			final Packet packet = WorldIO.load(folder);
			packet.world.createShadowMap(false);
			return packet;
		}
		
		packet.isLoad = false;
		packet.world = new World(folder, generator);
		packet.props = new Properties();
		return packet;
	}
	
	public String getStatus() {
		if (isSaving) return "Saving..";
		return exists ? "Loading.." : generator.getStatus();
	}
}
