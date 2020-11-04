package com.travall.game.world.chunk;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.glutils.VBO;
import com.travall.game.glutils.VertContext;
import com.travall.game.utils.math.ChunkPlane;

public class ChunkMesh implements Disposable 
{
	private final VBO vbo;
	private final int byteSize;
	private int count;
	
	public boolean isEmpty;
	public boolean isDirty;
	
	private float xPos, yPos, zPos;

	public ChunkMesh(ByteBuffer buffer, FloatArray verts, VertContext context, int glDraw) {
		byteSize = context.getAttrs().vertexSize;
		if (verts.isEmpty()) {
			isEmpty = true;
			buffer.limit(0);
			buffer.position(0);
			vbo = new VBO(buffer, context, glDraw, true);
			return;
		}
		count = (verts.size / byteSize) * 6;
		BufferUtils.copy(verts.items, buffer, verts.size, 0);
		vbo = new VBO(buffer, context, glDraw, true);
	}
	
	public ChunkMesh setPos(float x, float y, float z) {
		xPos = x;
		yPos = y;
		zPos = z;
		return this;
	}
	
	public void render() {
		if (isEmpty) return;
		vbo.bind();
		Gdx.gl.glDrawElements(GL30.GL_TRIANGLES, count, GL30.GL_UNSIGNED_SHORT, 0);
		vbo.unbind(false);
	}
	
	public void setVertices(FloatArray verts) {
		if (verts.isEmpty()) {
			isEmpty = true;
			return;
		}
		vbo.setVertices(verts.items, 0, verts.size);
		count = (verts.size / byteSize) * 6;
		isEmpty = false;
	}
	
	public boolean isVisable(final ChunkPlane[] planes) {
		final int size = planes.length;
		for (int i = 2; i < size; i++) {
			final ChunkPlane plane = planes[i];

			final float dist = plane.normal.dot(xPos, yPos, zPos) + plane.d;
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
	
	// For future optimization.
	public int getVAOhandle() {
		return vbo.getVAOhandle();
	}

	@Override
	public void dispose() {
		vbo.dispose();
	}
}
