package com.travall.game.glutils;

import static com.badlogic.gdx.Gdx.gl30;
import static com.badlogic.gdx.graphics.GL30.GL_ARRAY_BUFFER;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

/** An VertexBufferObject with VAO. GL30 only. */
public final class VBO implements Disposable
{
	private final static IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
	
	final VertContext context;
	final ByteBuffer buffer;
	final int glDraw;
	int bufferHandle;
	boolean isBound;
	
	public int vaoHandle;
	
	public VBO(ByteBuffer buffer, VertContext context, int glDraw, boolean usingQuadIndex) {
		this.context = context;
		this.glDraw = glDraw;
		this.buffer = buffer;
		upload(usingQuadIndex);
		gl30.glBindVertexArray(0);
	}
	
	public void setVertices(float[] vertices, int offset, int count) {
		BufferUtils.copy(vertices, buffer, count, offset);
		if (!isBound) gl30.glBindVertexArray(vaoHandle);
		gl30.glBindBuffer(GL_ARRAY_BUFFER, bufferHandle);
		gl30.glBufferData(GL_ARRAY_BUFFER, buffer.limit(), buffer, glDraw);
	}

	public void bind() {
		gl30.glBindVertexArray(vaoHandle);
		isBound = true;
	}
	
	public void unbind() {
		//gl30.glBindVertexArray(0); // Try to uncomment this line if there a graphic issues, or add the gl30.glBindVertexArray(0) at the end of the rendering.
		isBound = false;
	}

	/** Upload to GPU. */
	private void upload(boolean usingQuadIndex) 
	{
		// Create the VAO handle.
		tmpHandle.clear();
		gl30.glGenVertexArrays(1, tmpHandle);
		vaoHandle = tmpHandle.get();
		gl30.glBindVertexArray(vaoHandle);
		
		// Attach QuadIndexBuffer to the current VAO for quad rendering.
		if (usingQuadIndex)	QuadIndexBuffer.attach();
		
		// Create the buffer handle.
		bufferHandle = gl30.glGenBuffer();
		
		// Bind the buffer.
		gl30.glBindBuffer(GL_ARRAY_BUFFER, bufferHandle);
		
		// Upload the data.
		gl30.glBufferData(GL_ARRAY_BUFFER, buffer.limit(), buffer, glDraw);
		
		// Enable vertex attributes and set the pointers.
		final VertexAttributes attributes = context.getAttrs();
		final int numAttributes = attributes.size();
		for (int i = 0; i < numAttributes; ++i) {
			final VertexAttribute attribute = attributes.get(i);
			final int location = context.getLocation(i);
			context.getShader().enableVertexAttribute(location);

			context.getShader().setVertexAttribute(location, attribute.numComponents, attribute.type, 
					attribute.normalized, attributes.vertexSize, attribute.offset);
		}
	}

	@Override
	public void dispose() {
		gl30.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl30.glDeleteBuffer(bufferHandle);
		
		tmpHandle.clear();
		tmpHandle.put(vaoHandle);
		tmpHandle.flip();
		gl30.glDeleteVertexArrays(1, tmpHandle);
	}
}

