package com.travall.game.tools;

import static com.badlogic.gdx.Gdx.gl;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.glutils.VBO;
import com.travall.game.glutils.VertContext;

public final class Picker {
	
	private static final VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position());
	
	private static final int allVextices = 4*6; // 4*6 = full-cube
	private static final int allFloats = allVextices*3; // 3 = positions (floats)
	private static final int allIndex = (allVextices/4)*6; // 3 = positions (floats)
	
	private static VBO vbo;
	private static ShaderProgram shader;
	
	public static void ints() {
		shader = new ShaderProgram(Gdx.files.internal("Shaders/picker.vert"), Gdx.files.internal("Shaders/picker.frag"));
		VertContext context = new VertContext() {
			public ShaderProgram getShader() {
				return shader;
			}
			public VertexAttributes getAttrs() {
				return attributes;
			}
		};
		
		shader.begin();
		shader.setUniformf("u_alpha", 0.35f);
		shader.end();
		
		final FloatArray array = new FloatArray(allFloats);
		final float tmpN = -0.01f;
		float tmpP = (-tmpN)+1f;
		
		// facing Y+
		array.add(tmpP, tmpP, tmpN);
		array.add(tmpN, tmpP, tmpN);
		array.add(tmpN, tmpP, tmpP);
		array.add(tmpP, tmpP, tmpP);
		
		// facing Y-
		array.add(tmpN, tmpN, tmpN);
		array.add(tmpP, tmpN, tmpN);
		array.add(tmpP, tmpN, tmpP);
		array.add(tmpN, tmpN, tmpP);
		
		// facing Z-
		array.add(tmpN, tmpN, tmpN);
		array.add(tmpN, tmpP, tmpN);
		array.add(tmpP, tmpP, tmpN);
		array.add(tmpP, tmpN, tmpN);
		
		// facing X-
		array.add(tmpN, tmpN, tmpP);
		array.add(tmpN, tmpP, tmpP);
		array.add(tmpN, tmpP, tmpN);
		array.add(tmpN, tmpN, tmpN);
		
		// facing Z+
		array.add(tmpP, tmpN, tmpP);
		array.add(tmpP, tmpP, tmpP);
		array.add(tmpN, tmpP, tmpP);
		array.add(tmpN, tmpN, tmpP);
		
		// facing X+
		array.add(tmpP, tmpN, tmpN);
		array.add(tmpP, tmpP, tmpN);
		array.add(tmpP, tmpP, tmpP);
		array.add(tmpP, tmpN, tmpP);
		
		final FloatBuffer buffer = BufferUtils.newFloatBuffer(allFloats);
		BufferUtils.copy(array.items, buffer, allFloats, 0);
		vbo = new VBO(buffer, context, GL20.GL_STATIC_DRAW, true);
	}
	
	public static void render(Camera camera, GridPoint3 position) {
		if (position.y == -1) return;
		gl.glEnable(GL20.GL_BLEND);
		shader.begin();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		shader.setUniformf("u_trans", position.x, position.y, position.z);
		vbo.bind();
		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, allIndex, GL20.GL_UNSIGNED_SHORT, 0);
		vbo.unbind(); Gdx.gl30.glBindVertexArray(0);
		shader.end();
		gl.glDisable(GL20.GL_BLEND);
	}

	public static void dispose() {
		shader.dispose();
		vbo.dispose();
		vbo = null; // Tells JVM to remove/dispose FloatBuffer.
	}
}
