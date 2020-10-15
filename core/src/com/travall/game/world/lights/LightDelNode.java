package com.travall.game.world.lights;

import com.badlogic.gdx.utils.Pool;

public class LightDelNode {
	static final Pool<LightDelNode> POOL = new Pool<LightDelNode>(64) {
		protected LightDelNode newObject() {
			return new LightDelNode();
		}
	};
	
	public int x, y, z;
	public int val;
	
	public LightDelNode set(int x, int y, int z, int val) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.val = val;
		return this;
	}
}
