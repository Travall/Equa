package com.travall.game.renderer.vertices;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.graphics.glutils.ShaderProgram.POSITION_ATTRIBUTE;
import static com.badlogic.gdx.graphics.glutils.ShaderProgram.TEXCOORD_ATTRIBUTE;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.travall.game.glutils.QuadIndexBuffer;
import com.travall.game.glutils.VertContext;
import com.travall.game.utils.Utils;

// Needs update comments after attribute change.
/** The static class contains vertex attributes and shader */
public final class VoxelTerrain {
	// Data[sideLight&Ambiant, source-light, sunlight, unused]
	/** 3 Position, 4 Data (Packed into 1 float) and 2 TextureCoordinates [x,y,z,d,u,v] */
	public static final VertexAttributes attributes = new VertexAttributes(
			 	new VertexAttribute(Usage.Position, 3, POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4, "a_data"),
				new VertexAttribute(Usage.TextureCoordinates, 2, TEXCOORD_ATTRIBUTE)
			);
	
	/** 24 bytes in a single vertex with 6 float components. */ 
	public static final int byteSize = attributes.vertexSize;
	
	/** 6 floats in a single vertex. */ 
	public static final int floatSize = byteSize/Float.BYTES;
	
	public static ShaderProgram shaderProgram;
	public static int[] locations;
	
	public static ByteBuffer BUFFER;

	private static int toggleAO = 1;
	
	public static void ints() {
		shaderProgram = new ShaderProgram(files.internal("Shaders/voxel.vert"), files.internal("Shaders/voxel.frag"));
		locations = Utils.locateAttributes(shaderProgram, attributes);
		
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
		shaderProgram.bind();
		shaderProgram.setUniformMatrix("u_projTrans", cam.combined);
		//shaderProgram.setUniformf("sunLightIntensity", MathUtils.clamp(MathUtils.sin(sine)+0.5f, 0.0f, 1.0f));
		shaderProgram.setUniformf("sunLightIntensity", 1f);
		shaderProgram.setUniformf("brightness", 0.4f);
		shaderProgram.setUniformi("toggleAO", toggleAO);
	}
	
	/** End the shader. */
	public static void end() {
		Gdx.gl.glUseProgram(0);
	}
	
	public static void dispose() {
		shaderProgram.dispose();
		BufferUtils.disposeUnsafeByteBuffer(BUFFER);
		QuadIndexBuffer.dispose();
	}	
	
	public final static VertContext context = new VertContext() {
		public VertexAttributes getAttrs() {
			return attributes;
		}
		public ShaderProgram getShader() {
			return shaderProgram;
		}
		public int getLocation(int i) {
			return locations[i];
		}
	};


	public static void toggleAO() {
		toggleAO = toggleAO == 1 ? 0 : 1;
	}
}
