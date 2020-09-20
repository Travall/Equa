package com.travall.game.tools;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.graphics.glutils.ShaderProgram.POSITION_ATTRIBUTE;
import static com.badlogic.gdx.graphics.glutils.ShaderProgram.TEXCOORD_ATTRIBUTE;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.travall.game.glutils.VertContext;

// Needs update comments after attribute change.
/** The static class contains vertex attributes and shader */
public final class VoxelTerrain {
	// Data[sideLight&Ambiant, source-light, skylight, unused]
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
	
	public static void ints() {
		shaderProgram = new ShaderProgram(files.internal("Shaders/voxel.vert"), files.internal("Shaders/voxel.frag"));
		locations = Utils.locateAttributes(shaderProgram, attributes);
	}
	
	/** Begins the shader. */
	public static void begin(Camera cam) {
		shaderProgram.begin();
		shaderProgram.setUniformMatrix("u_projTrans", cam.combined);
	}
	
	/** End the shader. */
	public static void end() {
		shaderProgram.end();
	}
	
	public static void dispose() {
		shaderProgram.dispose();
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
}
