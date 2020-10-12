package com.travall.game.renderer;

import static com.badlogic.gdx.Gdx.gl;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.glutils.VBO;
import com.travall.game.glutils.VertContext;
import com.travall.game.handles.Raycast.RayInfo;

public final class Picker {
	
	private static final VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position());
	
	private static final int allVextices = 4*6; // 4*6 = full-cube
	private static final int allFloats = allVextices*3; // 3 = positions (floats)
	private static final int allIndex = (allVextices/4)*6; // 3 = positions (floats)
	private static final FloatArray array = new FloatArray(allFloats);
	private static final Vector3 out = new Vector3();
	
	private static VBO vbo;
	private static ShaderProgram shader;
	
	public static final RayInfo rayInfo = new RayInfo();
	public static boolean hasHit;
	
	public static void ints() {
		shader = new ShaderProgram(Gdx.files.internal("Shaders/picker.vert"), Gdx.files.internal("Shaders/picker.frag"));
		final VertContext context = new VertContext() {
			public ShaderProgram getShader() {
				return shader;
			}
			public VertexAttributes getAttrs() {
				return attributes;
			}
		};
		
		final FloatBuffer buffer = BufferUtils.newFloatBuffer(allFloats);
		buffer.position(0);
		buffer.limit(0);
		vbo = new VBO(buffer, context, GL20.GL_DYNAMIC_DRAW, true);
	}
	
	static float sine = 0;
	
	public static void render(Camera camera) {
		sine += 0.15f;
		if (sine > MathUtils.PI2) {
			sine -= MathUtils.PI2;
		}
		if (!hasHit) return;
		
		gl.glEnable(GL20.GL_BLEND);
		shader.bind();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		shader.setUniformf("u_trans", rayInfo.in.x, rayInfo.in.y, rayInfo.in.z);
		shader.setUniformf("u_alpha", (MathUtils.sin(sine)*0.1f)+0.25f);
		vbo.bind();
		updateVertex();
		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, allIndex, GL20.GL_UNSIGNED_SHORT, 0);
		vbo.unbind(); Gdx.gl30.glBindVertexArray(0);
		Gdx.gl.glUseProgram(0);
		gl.glDisable(GL20.GL_BLEND);
	}
	
	private static void updateVertex() {
		array.clear();
		
		final BoundingBox box = rayInfo.boxHit;
		final float tmpP = 0.003f; // extend the box.
		final float tmpN = -tmpP;
		final Vector3 out = Picker.out;
		
		// facing Y+
		box.getCorner110(out);
		array.add(tmpP+out.x, tmpP+out.y, tmpN+out.z);
		box.getCorner010(out);
		array.add(tmpN+out.x, tmpP+out.y, tmpN+out.z);
		box.getCorner011(out);
		array.add(tmpN+out.x, tmpP+out.y, tmpP+out.z);
		box.getCorner111(out);
		array.add(tmpP+out.x, tmpP+out.y, tmpP+out.z);
		
		// facing Y-
		box.getCorner000(out);
		array.add(tmpN+out.x, tmpN+out.y, tmpN+out.z);
		box.getCorner100(out);
		array.add(tmpP+out.x, tmpN+out.y, tmpN+out.z);
		box.getCorner101(out);
		array.add(tmpP+out.x, tmpN+out.y, tmpP+out.z);
		box.getCorner001(out);
		array.add(tmpN+out.x, tmpN+out.y, tmpP+out.z);
		
		// facing Z-
		box.getCorner000(out);
		array.add(tmpN+out.x, tmpN+out.y, tmpN+out.z);
		box.getCorner010(out);
		array.add(tmpN+out.x, tmpP+out.y, tmpN+out.z);
		box.getCorner110(out);
		array.add(tmpP+out.x, tmpP+out.y, tmpN+out.z);
		box.getCorner100(out);
		array.add(tmpP+out.x, tmpN+out.y, tmpN+out.z);
		
		// facing X-
		box.getCorner001(out);
		array.add(tmpN+out.x, tmpN+out.y, tmpP+out.z);
		box.getCorner011(out);
		array.add(tmpN+out.x, tmpP+out.y, tmpP+out.z);
		box.getCorner010(out);
		array.add(tmpN+out.x, tmpP+out.y, tmpN+out.z);
		box.getCorner000(out);
		array.add(tmpN+out.x, tmpN+out.y, tmpN+out.z);
		
		// facing Z+
		box.getCorner101(out);
		array.add(tmpP+out.x, tmpN+out.y, tmpP+out.z);
		box.getCorner111(out);
		array.add(tmpP+out.x, tmpP+out.y, tmpP+out.z);
		box.getCorner011(out);
		array.add(tmpN+out.x, tmpP+out.y, tmpP+out.z);
		box.getCorner001(out);
		array.add(tmpN+out.x, tmpN+out.y, tmpP+out.z);
		
		// facing X+
		box.getCorner100(out);
		array.add(tmpP+out.x, tmpN+out.y, tmpN+out.z);
		box.getCorner110(out);
		array.add(tmpP+out.x, tmpP+out.y, tmpN+out.z);
		box.getCorner111(out);
		array.add(tmpP+out.x, tmpP+out.y, tmpP+out.z);
		box.getCorner101(out);
		array.add(tmpP+out.x, tmpN+out.y, tmpP+out.z);
		
		vbo.setVertices(array.items, 0, allFloats);
	}

	public static void dispose() {
		shader.dispose();
		vbo.dispose();
		vbo = null; // Tells JVM to remove/dispose FloatBuffer.
	}
}
