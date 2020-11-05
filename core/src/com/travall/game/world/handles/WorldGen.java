package com.travall.game.world.handles;

import com.travall.game.utils.AsyncThreaded;
import com.travall.game.world.World;
import com.travall.game.world.gen.DefaultGen;
import com.travall.game.world.gen.Generator;

public class WorldGen extends AsyncThreaded<World> {

	public final Generator generator = new DefaultGen();
	
	public WorldGen() {
		super("World Generator");
	}
	
	public void run() {
		result = exe.submit(this);
	}

	@Override
	public World call() throws Exception {
		return new World(generator);
	}
	
	public String getStatus() {
		return generator.getStatus();
	}
}
