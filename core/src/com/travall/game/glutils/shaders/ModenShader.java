package com.travall.game.glutils.shaders;

import static com.badlogic.gdx.Gdx.gl;
import static com.travall.game.utils.Utils.intbuf;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectIntMap;

public final class ModenShader implements ShaderHandle, Disposable {
	/** Uniform lookup. */
	private final ObjectIntMap<String> uniforms = new ObjectIntMap<>();
	
	/** Attribute lookup. */
	private final ObjectIntMap<String> attributes = new ObjectIntMap<>();
	
	/** Program handle. **/
	private final int program;
	
	private ShaderPart vertShader, geomShader, fragShader;
	
	public ModenShader() {
		program = gl.glCreateProgram();
	}
	
	/** Must <code>link()</code> after shader change. */
	public void setShader(ShaderPart shader) {
		final ShaderPart lastShader = getShader(shader.type);
		if (lastShader != null) {
			gl.glDetachShader(program, lastShader.handle);
		}
		gl.glAttachShader(program, shader.handle);
		
		switch (shader.type) {
		case VERTEX:   vertShader = shader; break;
		case GEOMETRY: geomShader = shader; break;
		case FRAGMENT: fragShader = shader; break;
		}
	}
	
	/** Must <code>link()</code> after shader change. */
	public void removeShader(ShaderType type) {
		final ShaderPart lastShader = getShader(type);
		if (lastShader != null) {
			gl.glDetachShader(program, lastShader.handle);
		}
		
		switch (type) {
		case VERTEX:   vertShader = null; break;
		case GEOMETRY: geomShader = null; break;
		case FRAGMENT: fragShader = null; break;
		}
	}
	
	public ShaderPart getShader(ShaderType type) {
		switch (type) {
		case VERTEX:   return vertShader;
		case GEOMETRY: return geomShader;
		case FRAGMENT: return fragShader;
		default: return null;
		}
	}
	
	public void link() {
		gl.glLinkProgram(program);
		
		intbuf.clear();
		gl.glGetProgramiv(program, GL20.GL_LINK_STATUS, intbuf);
		if (intbuf.get(0) == 0) {
			final String log = gl.glGetProgramInfoLog(program);
			dispose();
			throw new RuntimeException(log);
		}
		
		fetchUniforms();
		fetchAttributes();
	}
	
	private void fetchUniforms() {
		intbuf.clear();
		gl.glGetProgramiv(program, GL20.GL_ACTIVE_UNIFORMS, intbuf);
		final int len = intbuf.get(0);

		uniforms.clear();
		for (int i = 0; i < len; i++) {
			String name = gl.glGetActiveUniform(program, i, intbuf, intbuf);
			uniforms.put(name, gl.glGetUniformLocation(program, name));
		}
	}

	private void fetchAttributes() {
		intbuf.clear();
		gl.glGetProgramiv(program, GL20.GL_ACTIVE_ATTRIBUTES, intbuf);
		final int len = intbuf.get(0);

		attributes.clear();
		for (int i = 0; i < len; i++) {
			String name = gl.glGetActiveAttrib(program, i, intbuf, intbuf);
			attributes.put(name, gl.glGetAttribLocation(program, name));
		}
	}
	
	/** Fetch Attribute Location. */
	public int getAttLoc(String alias) {
		return attributes.get(alias, -1);
	}
	
	/** Fetch Uniform Location. */
	public int getUniLoc(String alias) {
		return uniforms.get(alias, -1);
	}
	
	public void enableVertexAttribute(String alias) {
		gl.glEnableVertexAttribArray(getAttLoc(alias));
	}
	
	@Override
	public void setVertexAttributes(VertexAttributes attributes) {
		final int len = attributes.size();
		for (int i = 0; i < len; ++i) {
			final VertexAttribute attribute = attributes.get(i);
			final int location = getAttLoc(attribute.alias);

			gl.glEnableVertexAttribArray(location);

			gl.glVertexAttribPointer(location, attribute.numComponents, attribute.type, 
					attribute.normalized, attributes.vertexSize, attribute.offset);
		}
	}
	
	public void bind() {
		gl.glUseProgram(program);
	}

	@Override
	public void dispose() {
		gl.glUseProgram(0);
		gl.glDeleteProgram(program);
	}
}
