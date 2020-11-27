package com.travall.game.glutils.shaders;

import com.badlogic.gdx.graphics.GL20;
import com.travall.game.glutils.GL32;

public enum ShaderType {
	VERTEX(GL20.GL_VERTEX_SHADER), GEOMETRY(GL32.GL_GEOMETRY_SHADER), FRAGMENT(GL20.GL_FRAGMENT_SHADER);
	
	public final int glShader;
	
	private ShaderType(int glShader) {
		this.glShader = glShader;
	}
}
