package com.travall.game.world.gen;

import com.travall.game.world.World;

public abstract class Generator 
{
	private volatile String status = "Installation..";
	
	public final String getStatus() {
		return status;
	}
	
	protected final void setStatus(final String status) {
		this.status = status;
	}
	
	public abstract void genrate(World world);
}
