package com.travall.game.world.lights;

import com.badlogic.gdx.utils.Pool;

final class LightNode {
	static final Pool<LightNode> POOL = new Pool<LightNode>(64) {
		protected LightNode newObject() {
			return new LightNode();
		}
	};
	
	public int x, y, z;
	
	LightNode() {
	}
	
	public LightNode set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		final LightNode node = (LightNode)obj;
		return node.x == x && node.y == y && node.z == z;
	}
}
