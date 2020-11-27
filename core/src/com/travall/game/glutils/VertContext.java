package com.travall.game.glutils;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.travall.game.glutils.shaders.ShaderHandle;

public interface VertContext 
{
	public ShaderHandle getShader();
	public VertexAttributes getAttrs();
	
	public default void setVertexAttributes() {
		getShader().setVertexAttributes(getAttrs());
	}
}
