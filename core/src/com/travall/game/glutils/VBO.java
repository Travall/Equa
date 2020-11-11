package com.travall.game.glutils;

import java.nio.Buffer;

import com.badlogic.gdx.utils.BufferUtils;

/** An VertexBufferObject with VAO. GL30 only. */
public class VBO extends VBObase {
	public VBO(Buffer buffer, VertContext context, int glDraw, boolean usingQuadIndex) {
		this.glDraw = glDraw;
		this.buffer = buffer;
		upload(context, usingQuadIndex);
	}
	
	public void setVertices(float[] vertices, int offset, int count) {
		BufferUtils.copy(vertices, buffer, count, offset);
		updateVertex();
	}
	
	public void setVertices(int[] vertices, int offset, int count) {
		BufferUtils.copy(vertices, offset, count, buffer);
		updateVertex();
	}
}
