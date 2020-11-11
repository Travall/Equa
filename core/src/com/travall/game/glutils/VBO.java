package com.travall.game.glutils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

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
		if (buffer instanceof ByteBuffer)
			buffer.limit(count << 2);
		else if (buffer instanceof IntBuffer)
			buffer.limit(count);

		BufferUtils.copy(vertices, offset, count, buffer);
		buffer.position(0);
		updateVertex();
	}
}
