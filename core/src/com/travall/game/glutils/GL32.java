package com.travall.game.glutils;

import static com.badlogic.gdx.Gdx.gl;

public final class GL32 {
	private GL32() {}

	public static final int GL_GEOMETRY_SHADER = 0x8DD9;
	public static final int GL_TESS_EVALUATION_SHADER = 0x8E87;
	public static final int GL_TESS_CONTROL_SHADER = 0x8E88;
	public static final int GL_PATCHES = 0xE;
	
	
	// Expermental OpenGL Multi-Draw Technology Emulation. 
	
	public static void glMultiDrawArrays(int mode, int[] first, int[] count, int primcount)
	{
		for (int i = 0; i < primcount; i++)
		{
			if (count[i] > 0) {
				gl.glDrawArrays(mode, first[i], count[i]);
			}
		}
	}

	public static void glMultiDrawElements(int mode, int[] count, int type, int[] indices, int primcount)
	{
		for (int i = 0; i < primcount; i++)
		{
			if (count[i] > 0) {
				gl.glDrawElements(mode, count[i], type, indices[i]);
			}
		}
	}
}
