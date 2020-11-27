package com.travall.game.glutils.shaders;

import static com.badlogic.gdx.Gdx.gl;
import static com.travall.game.utils.Utils.intbuf;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public final class ShaderPart implements Disposable {
	
	public final FileHandle file;
	public final ShaderType type;
	public final int handle;
	
	public ShaderPart(FileHandle file, ShaderType type) {
		this.file = file;
		this.type = type;
		this.handle = gl.glCreateShader(type.glShader);
		compile();
		SHADERS.add(this);
	}
	
	public void compile() {
		gl.glShaderSource(handle, file.readString());
		gl.glCompileShader(handle);
		
		intbuf.clear();
		gl.glGetShaderiv(handle, GL20.GL_COMPILE_STATUS, intbuf);
		if (intbuf.get(0) == 0) {
			final String log = gl.glGetShaderInfoLog(handle);
			dispose();
			throw new RuntimeException(log);
		}
	}

	@Override
	public void dispose() {
		gl.glDeleteShader(handle);
		SHADERS.removeValue(this, true);
	}
	
	private static final Array<ShaderPart> SHADERS = new Array<>(false, 10);
	
	public static void removeAll() {
		for (ShaderPart shader : SHADERS) shader.dispose();
		SHADERS.clear();
	}
}
