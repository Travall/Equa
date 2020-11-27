package com.travall.game.renderer.vertices;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.gl;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.travall.game.glutils.QuadIndexBuffer;
import com.travall.game.glutils.VertContext;
import com.travall.game.glutils.shaders.ModenShader;
import com.travall.game.glutils.shaders.ShaderHandle;
import com.travall.game.glutils.shaders.ShaderPart;
import com.travall.game.glutils.shaders.ShaderType;

// Needs update comments after attribute change.
/** The static class contains vertex attributes and shader */
public final class VoxelTerrain {
	// Data[sideLight&Ambiant, source-light, sunlight, unused]
	/** 3 Position, 4 Data (Packed into 1 float) and 2 TextureCoordinates [x,y,z,d,u,v] */
	public static final VertexAttributes attributes = new VertexAttributes(
			 	new VertexAttribute(Usage.Position, 3, "position"),
				new VertexAttribute(Usage.ColorPacked, 4, "data"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "texCoord")
			);
	
	/** 24 bytes in a single vertex with 6 float components. */ 
	public static final int byteSize = attributes.vertexSize;
	
	/** 6 floats in a single vertex. */ 
	public static final int floatSize = byteSize/Float.BYTES;
	
	public static ModenShader shader;
	
	public static ByteBuffer BUFFER;

	private static int toggleAO = 1;
	
	public static void ints() {
		shader = new ModenShader();
		shader.setShader(new ShaderPart(files.internal("Shaders/voxel.vert"), ShaderType.VERTEX));
		shader.setShader(new ShaderPart(files.internal("Shaders/default.frag"), ShaderType.FRAGMENT));
		shader.link();
		
		// 1,572,864 bytes of data, or 1.57MB.
		BUFFER = BufferUtils.newUnsafeByteBuffer(QuadIndexBuffer.maxVertex*byteSize);
		
		QuadIndexBuffer.ints();
	}
	
	static float sine = 0;
	
	/** Begins the shader. */
	public static void begin(Camera cam) {
		sine += 0.01f;
		if (sine > MathUtils.PI2) {
			sine -= MathUtils.PI2;
		}
		shader.bind();
		gl.glUniformMatrix4fv(shader.getUniLoc("projTrans"), 1, false, cam.combined.val, 0);
		gl.glUniform1f(shader.getUniLoc("sunLightIntensity"), 1f);
		gl.glUniform1f(shader.getUniLoc("brightness"), 0.0f);
		gl.glUniform1i(shader.getUniLoc("toggleAO"), toggleAO);
	}
	
	/** End the shader. */
	public static void end() {
		Gdx.gl.glUseProgram(0);
	}
	
	public static void dispose() {
		shader.dispose();
		BufferUtils.disposeUnsafeByteBuffer(BUFFER);
		QuadIndexBuffer.dispose();
	}	
	
	public final static VertContext context = new VertContext() {
		public VertexAttributes getAttrs() {
			return attributes;
		}
		public ShaderHandle getShader() {
			return shader;
		}
	};


	public static void toggleAO() {
		toggleAO = toggleAO == 1 ? 0 : 1;
	}
}
