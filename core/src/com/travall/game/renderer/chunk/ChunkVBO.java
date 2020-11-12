package com.travall.game.renderer.chunk;

import static com.travall.game.renderer.vertices.VoxelTerrain.byteSize;
import static com.travall.game.renderer.vertices.VoxelTerrain.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.glutils.VBObase;
import com.travall.game.renderer.vertices.VoxelTerrain;

public class ChunkVBO extends VBObase {
	
	private int count;
	public boolean isEmpty;

	public ChunkVBO(FloatArray array) {
		glDraw = GL30.GL_STREAM_DRAW;
		buffer = VoxelTerrain.BUFFER;
		
		if (array.isEmpty()) {
			isEmpty = true;
			buffer.limit(0);
		} else {
			BufferUtils.copy(array.items, buffer, array.size, 0);
			count = (array.size / byteSize) * 6;
		}
		
		upload(context, true);
	}

	public void render() {
		if (isEmpty) return;
		bind();
		Gdx.gl.glDrawElements(GL30.GL_TRIANGLES, count, GL30.GL_UNSIGNED_SHORT, 0);
		unbind(false);
	}
	
	public void setVertices(FloatArray vertices) {
		if (vertices.isEmpty()) {
			isEmpty = true;
			return;
		}
		BufferUtils.copy(vertices.items, buffer, vertices.size, 0);
		updateVertex();
		count = (vertices.size / byteSize) * 6;
		isEmpty = false;
	}
}
