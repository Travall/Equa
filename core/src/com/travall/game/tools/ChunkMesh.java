package com.travall.game.tools;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.glutils.VBO;
import com.travall.game.glutils.VertContext;

public class ChunkMesh implements Disposable 
{
	private final VBO vbo;
	private final int byteSize;
	private int count;
	
	private boolean isEmpty;

	public ChunkMesh(ByteBuffer buffer, FloatArray verts, VertContext context, int glDraw) {
		byteSize = context.getAttrs().vertexSize;
		count = (verts.size / byteSize) * 6;
		BufferUtils.copy(verts.items, buffer, verts.size, 0);
		vbo = new VBO(buffer, context, glDraw, true);
	}
	
	public void render() {
		if (isEmpty) return;
		vbo.bind();
		Gdx.gl.glDrawElements(GL30.GL_TRIANGLES, count, GL30.GL_UNSIGNED_SHORT, 0);
		vbo.unbind();
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
	
	public boolean isEmpty() {
		return isEmpty;
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
