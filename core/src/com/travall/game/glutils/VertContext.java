package com.travall.game.glutils;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface VertContext 
{
	public ShaderProgram getShader();
	public VertexAttributes getAttrs();
	public int getLocation(int i);
	
	/** @return <code>getAttrs().vertexSize/Float.BYTE</code> */
	public default int getFloatSize() {
		return getAttrs().vertexSize/Float.BYTES;
	}
}
