package com.travall.game.glutils.shaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;

public class ShaderProgram extends com.badlogic.gdx.graphics.glutils.ShaderProgram implements ShaderHandle {

	public ShaderProgram(FileHandle vertexShader, FileHandle fragmentShader) {
		super(vertexShader, fragmentShader);
	}

	@Override
	public void setVertexAttributes(VertexAttributes attributes) {
		final int len = attributes.size();
		for (int i = 0; i < len; ++i) {
			final VertexAttribute attribute = attributes.get(i);
			final int location = getAttributeLocation(attribute.alias);

			enableVertexAttribute(location);

			setVertexAttribute(location, attribute.numComponents, attribute.type, 
				attribute.normalized, attributes.vertexSize, attribute.offset);
		}
	}
}
