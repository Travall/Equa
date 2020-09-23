package com.travall.game.tools;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.blocks.Block;
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
			if (items.length == maxFloats) throw new IllegalStateException("Max vertex size has been reached!");
			return super.resize(Math.min(newSize, maxFloats));
		}
	};
	
//		v2-----v3
//		|       |
//		|       |
//		v1-----v4
	public void rect(TextureRegion textureRegion) {
		vertices.add(v1.x, v1.y, v1.z, v1.packData());
		vertices.add(textureRegion.getU2(), textureRegion.getV2());
		
		vertices.add(v2.x, v2.y, v2.z, v2.packData());
		vertices.add(textureRegion.getU2(), textureRegion.getV());
		
		vertices.add(v3.x, v3.y, v3.z, v3.packData());
		vertices.add(textureRegion.getU(), textureRegion.getV());
		
		vertices.add(v4.x, v4.y, v4.z, v4.packData());
		vertices.add(textureRegion.getU(), textureRegion.getV2());
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
	
	public ChunkMesh end(ChunkMesh mesh) {
		mesh.setVertices(vertices);
		mesh.isDirty = false;
		return mesh;
	}
	
	
//		v2-----v3
//		|       |
//		|       |
//		v1-----v4
	public void buildCube(Block block, GridPoint3 pos, boolean renderTop, boolean renderBottom, boolean render1,
			boolean render2, boolean render3, boolean render4, float light) {
		
		setSrc(light/8f);

		float temp; // For optimization by reduce the converting from integer to float.
		if (renderTop) { // facing Y+
			temp = pos.y + 1;
			v1.setPos(pos.x+1, temp, pos.z);
			v2.setPos(pos.x,   temp, pos.z);
			v3.setPos(pos.x,   temp, pos.z+1);
			v4.setPos(pos.x+1, temp, pos.z+1);
			setAmb(1f);
			rect(block.textures.top);
		}
		if (renderBottom) { // facing Y-
			temp = pos.y;
			v1.setPos(pos.x,   temp, pos.z);
			v2.setPos(pos.x+1, temp, pos.z);
			v3.setPos(pos.x+1, temp, pos.z+1);
			v4.setPos(pos.x,   temp, pos.z+1);
			setAmb(0.6f);
			rect(block.textures.bottom);
		}
		if (render1) { // facing Z-
			temp = pos.z;
			v1.setPos(pos.x,   pos.y,   temp);
			v2.setPos(pos.x,   pos.y+1, temp);
			v3.setPos(pos.x+1, pos.y+1, temp);
			v4.setPos(pos.x+1, pos.y,   temp);
			setAmb(0.70f);
			rect(block.textures.render3);
		}
		if (render2) { // facing X-
			temp = pos.x;
			v1.setPos(temp, pos.y,   pos.z+1);
			v2.setPos(temp, pos.y+1, pos.z+1);
			v3.setPos(temp, pos.y+1, pos.z);
			v4.setPos(temp, pos.y,   pos.z);
			setAmb(0.85f);
			rect(block.textures.render3);
		}
		if (render3) { // facing Z+
			temp = pos.z + 1;
			v1.setPos(pos.x+1,   pos.y,   temp);
			v2.setPos(pos.x+1,   pos.y+1, temp);
			v3.setPos(pos.x, pos.y+1, temp);
			v4.setPos(pos.x, pos.y,   temp);
			setAmb(0.70f);
			rect(block.textures.render3);
		}
		if (render4) { // facing X+
			temp = pos.x + 1;
			v1.setPos(temp, pos.y,   pos.z);
			v2.setPos(temp, pos.y+1, pos.z);
			v3.setPos(temp, pos.y+1, pos.z+1);
			v4.setPos(temp, pos.y,   pos.z+1);
			setAmb(0.85f);
			rect(block.textures.render3);
		}
	}
	
	public void setAmb(float value) {
		v1.ambLit = value;
		v2.ambLit = value;
		v3.ambLit = value;
		v4.ambLit = value;
	}
	
	public void setSrc(float value) {
		v1.srcLit = value;
		v2.srcLit = value;
		v3.srcLit = value;
		v4.srcLit = value;
	}
	
	public void setSun(float value) {
		v1.sunLit = value;
		v2.sunLit = value;
		v3.sunLit = value;
		v4.sunLit = value;
	}
	
	public static class VertInfo {
		/** Positions */
		public float x, y, z;
		/** Lighting. Must clamp it to 0 to 1 if necessary. Use the <code>MathUtils.clamp(value, 0f, 1f)</code> */
		public float ambLit = 1f, srcLit, sunLit = 1f; 
//		/** Texture coordinates */
//		public float u, v; // Not needed.
		
		public float packData() {
			return Float.intBitsToFloat((((int)(255*sunLit)<<16)|((int)(255*srcLit)<<8)|((int)(255*ambLit))));
		}
		
		public void setPos(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
