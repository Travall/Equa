package com.travall.game.renderer.chunk;

import com.badlogic.gdx.utils.Disposable;
import com.travall.game.utils.math.ChunkPlane;

public class ChunkMesh implements Disposable 
{
	public final ChunkVBO opaqeVBO, transVBO;
	public boolean isDirty;

	public ChunkMesh(ChunkVBO opaqeVBO, ChunkVBO transVBO) {
		this.opaqeVBO = opaqeVBO;
		this.transVBO = transVBO;
	}
	
	public boolean isVisable(final ChunkPlane[] planes, float x, float y, float z) {
		for (final ChunkPlane plane : planes) {
			final float dist = plane.normal.dot(x, y, z) + plane.d;
			final float radius = plane.radius;
			
			if (dist > radius) {
				continue;
			}
			
			if (dist < -radius) {
				return false;
			}
			
			continue;
		}
		return true;
	}
	
	@Override
	public void dispose() {
		opaqeVBO.dispose();
		transVBO.dispose();
	}
}
