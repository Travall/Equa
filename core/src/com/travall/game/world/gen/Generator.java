package com.travall.game.world.gen;

import com.travall.game.world.World;

public abstract class Generator 
{
	private volatile String status = "Installation..";
	private final Object lock = new Object();
	
	public final String getStatus() {
		synchronized (lock) {
			return status;
		}
	}
	
	protected final void setStatus(final String status) {
		synchronized (lock) {
			this.status = status;
		}
	}
	
	public abstract void genrate(World world);
}
