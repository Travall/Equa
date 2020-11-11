package com.travall.game.glutils;

import static com.badlogic.gdx.Gdx.gl30;
import static com.badlogic.gdx.graphics.GL20.GL_ARRAY_BUFFER;

import java.nio.Buffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

public class VBObase implements Disposable {
	protected final static IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
	
	protected Buffer buffer;
	protected int glDraw;
	protected int bufferHandle, vaoHandle;
	protected boolean isBound;
	
	protected VBObase() {
		
	}
	
	public void bind() {
		gl30.glBindVertexArray(vaoHandle);
		isBound = true;
	}

	public void unbind(boolean unbindVAO) {
		if (unbindVAO) gl30.glBindVertexArray(0);
		isBound = false;
	}
	
	/** Upload to GPU. */
	protected void upload(VertContext context, boolean usingQuadIndex) {
		createHandles();

		// Upload the data.
		if (buffer.hasRemaining())
			gl30.glBufferData(GL_ARRAY_BUFFER, 0, buffer, glDraw);

		setAttributes(context);
		
		// Attach QuadIndexBuffer to the current VAO for quad rendering.
		if (usingQuadIndex) QuadIndexBuffer.attach();
		
		// unbind the VAO.
		gl30.glBindVertexArray(0);
	}
	
	protected final void updateVertex() {
		if (!isBound) gl30.glBindVertexArray(vaoHandle);
		gl30.glBindBuffer(GL_ARRAY_BUFFER, bufferHandle);
		gl30.glBufferData(GL_ARRAY_BUFFER, 0, buffer, glDraw);
		if (!isBound) gl30.glBindVertexArray(0);
	}
	
	/** Enable vertex attributes and set the pointers. */
	protected final void setAttributes(VertContext context) {
		final VertexAttributes attributes = context.getAttrs();
		final int numAttributes = attributes.size();
		for (int i = 0; i < numAttributes; ++i) {
			final VertexAttribute attribute = attributes.get(i);
			final int location = context.getLocation(i);

			final ShaderProgram shader = context.getShader();
			shader.enableVertexAttribute(location);

			shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized,
					attributes.vertexSize, attribute.offset);
		}
	}
	
	/** Create the VAO and buffer handle and bind it. */
	protected final void createHandles() {
		// Create the VAO and buffer handle.
		tmpHandle.clear();
		gl30.glGenVertexArrays(1, tmpHandle);
		vaoHandle = tmpHandle.get();
		gl30.glBindVertexArray(vaoHandle);

		// Create the buffer handle.
		bufferHandle = gl30.glGenBuffer();
		
		// Bind the buffer.
		gl30.glBindBuffer(GL_ARRAY_BUFFER, bufferHandle);
	}

	public final int getVAOhandle() {
		return vaoHandle;
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
