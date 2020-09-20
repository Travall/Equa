package com.travall.game.glutils;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.*;

import java.nio.ShortBuffer;

import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

/** This QuadIndexBuffer intention to render meshes with "quads" instead of triangles. GL30 only. */
public class QuadIndexBuffer implements Disposable {
	
	final int bufferHandle;

	public QuadIndexBuffer() {
		final int maxVertex = 98304; // 98304 is a maximum vertex size as possible.
		final ShortBuffer buffer = BufferUtils.newShortBuffer(maxVertex);
		buffer.position(0);
		
		// Indexing so it can reuse the same vertex to make up a quad.
		for (int i = 0, v = 0; i < maxVertex; i += 6, v += 4) { 
			buffer.put((short)v);
			buffer.put((short)(v+1));
			buffer.put((short)(v+2));
			buffer.put((short)(v+2));
			buffer.put((short)(v+3));
			buffer.put((short)v);
		}
		
		buffer.flip(); // Make it readable for GPU.
		
		// Upload the buffer to GPU, and let JVM dispose the buffer from RAM after finish uploading.
		bufferHandle = gl.glGenBuffer();
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer.limit(), buffer, GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	/** Attach it to the current VAO. */
	public void attach() {
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
	}
	
	@Override
	public void dispose() {
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(bufferHandle);
	}
}
