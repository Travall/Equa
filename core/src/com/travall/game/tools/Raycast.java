package com.travall.game.tools;

import static com.badlogic.gdx.math.MathUtils.floor;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.travall.game.world.World;

public class Raycast {
	private static final float LENGHT = 32.0f;
	private static final float STEPS  = 0.02f;
	private static final RayInfo info = new RayInfo();
	private static final Vector3 pos  = new Vector3();
	private static final Vector3 nor  = new Vector3();
	private static final GridPoint3 offset = new GridPoint3();
	
	
	public static RayInfo Fastcast(Camera cam, World map) {
		nor.set(cam.direction).scl(STEPS);
		
		final Vector3 rayPos = cam.position;
		offset.set(floor(rayPos.x), floor(rayPos.y), floor(rayPos.z));
		
		// if Block is inside the player, then cast that block
		if (map.blockExists(offset.x, offset.y, offset.z)) {
			info.in.set(offset);
			info.out.set(offset);
			return info;
		}
		
		pos.set(rayPos).sub(offset.x, offset.y, offset.z);
		
		int x  = 0, y  = 0, z  = 0; // Current integer position.
		int lx = 0, ly = 0, lz = 0; // Last integer position.
		
		for (float i = 0; i < LENGHT; i += STEPS) {
			pos.add(nor);
			if ((y = floor(pos.y)) == ly && (x = floor(pos.x)) == lx && (z = floor(pos.z)) == lz) continue;
			
			if (!map.blockExists(x+offset.x, y+offset.y, z+offset.z)) {
				lx = x; ly = y; lz = z;
				continue;
			}
			
			info.in.set(x, y, z).add(offset);
			info.out.set(lx, ly, lz).add(offset);
			return info;
		}
		
		return null;
	}
	
	public static class RayInfo {
		/** Outside the block. */
		public final GridPoint3 out = new GridPoint3();
		/** Inside the block. */
		public final GridPoint3 in  = new GridPoint3();
	}
}
