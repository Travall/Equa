package com.travall.game.tools;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.glutils.QuadIndexBuffer;

public class BlockBuilder {
	
	private static final int maxFloats = QuadIndexBuffer.maxVertex*VoxelTerrain.floatSize;
	
	public final VertInfo
	v1 = new VertInfo(),
	v2 = new VertInfo(),
	v3 = new VertInfo(),
	v4 = new VertInfo();
	
	private final FloatArray vertices = new FloatArray(256) {
		protected float[] resize (int newSize) {
			if (newSize == items.length) throw new IllegalStateException("Max vertex size has been reached!");
			return super.resize(Math.min(newSize, maxFloats));
		}
	};
	
//		v2-----v3
//		|       |
//		|       |
//		v1-----v4
	public void rect(TextureRegion textureRegion) {
		vertices.add(v1.x, v1.y, v1.z, v1.packData());
		vertices.add(textureRegion.getU(), textureRegion.getV());
		
		vertices.add(v2.x, v2.y, v2.z, v2.packData());
		vertices.add(textureRegion.getU(), textureRegion.getV2());
		
		vertices.add(v3.x, v3.y, v3.z, v3.packData());
		vertices.add(textureRegion.getU2(), textureRegion.getV2());
		
		vertices.add(v4.x, v4.y, v4.z, v4.packData());
		vertices.add(textureRegion.getU2(), textureRegion.getV());
	}	
	
	public void begin() {
		vertices.clear();
	}

	/** Will return null if vertices are empty.  */
	public ChunkMesh end(int glDraw) {
		if (vertices.notEmpty()) {
			return new ChunkMesh(VoxelTerrain.BUFFER, vertices, VoxelTerrain.context, glDraw);
		}
		return null;
	}
	
	public void end(ChunkMesh mesh) {
		mesh.setVertices(vertices);
	}
	
	public static class VertInfo {
		/** Positions */
		public float x, y, z;
		/** Lighting. Must clamp it to 0 to 1 if necessary. Use the <code>MathUtils.clamp(value, 0f, 1f)</code> */
		public float ambLit, srcLit, sunLit; 
//		/** Texture coordinates */
//		public float u, v; // Not needed.
		
		public float packData() {
			return Float.intBitsToFloat((((int)(255*sunLit)<<16)|((int)(255*srcLit)<<8)|((int)(255*ambLit))));
		}
	}
}
