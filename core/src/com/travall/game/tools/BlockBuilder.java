package com.travall.game.tools;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.blocks.Block;
import com.travall.game.world.World;
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
	
	private final World map;
	
	public BlockBuilder(World map) {
		this.map = map;
	}
	
//     v3-----v2
//     |       |
//     |       |
//     v4-----v1
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
		return new ChunkMesh(VoxelTerrain.BUFFER, vertices, VoxelTerrain.context, glDraw);
	}
	
	public ChunkMesh end(ChunkMesh mesh) {
		mesh.setVertices(vertices);
		mesh.isDirty = false;
		return mesh;
	}


//     v3-----v2
//     |       |
//     |       |
//     v4-----v1
	public void buildCube(Block block, GridPoint3 pos, boolean renderTop, boolean renderBottom, boolean render1,
			boolean render2, boolean render3, boolean render4) {

		float temp; // For optimization by reduce the converting from integer to float.
		final int x = pos.x, y = pos.y, z = pos.z;
		final BlockTextures textures = block.textures;

		if (renderTop) { // facing Y+
			final int y1 = y+1;
			temp = y1;
			
			v1.setPos(x+1, temp, z);
			v2.setPos(x,   temp, z);
			v3.setPos(x,   temp, z+1);
			v4.setPos(x+1, temp, z+1);
			
			setAmb(1f);
			v1.vertAO(map.blockExists(x+1, y1, z), map.blockExists(x, y1, z-1), map.blockExists(x+1, y1, z-1));
			v2.vertAO(map.blockExists(x-1, y1, z), map.blockExists(x, y1, z-1), map.blockExists(x-1, y1, z-1));
			v3.vertAO(map.blockExists(x-1, y1, z), map.blockExists(x, y1, z+1), map.blockExists(x-1, y1, z+1));
			v4.vertAO(map.blockExists(x+1, y1, z), map.blockExists(x, y1, z+1), map.blockExists(x+1, y1, z+1));
			
			final int center = map.getLight(x, y1, z);
			v1.smoothLight(center, map.getLight(x+1, y1, z), map.getLight(x, y1, z-1), map.getLight(x+1, y1, z-1));
			v2.smoothLight(center, map.getLight(x-1, y1, z), map.getLight(x, y1, z-1), map.getLight(x-1, y1, z-1));
			v3.smoothLight(center, map.getLight(x-1, y1, z), map.getLight(x, y1, z+1), map.getLight(x-1, y1, z+1));
			v4.smoothLight(center, map.getLight(x+1, y1, z), map.getLight(x, y1, z+1), map.getLight(x+1, y1, z+1));

			rect(textures.top);
		}
		if (renderBottom) { // facing Y-
			final int y1 = y-1;
			temp = pos.y;
			
			v1.setPos(x,   temp, z);
			v2.setPos(x+1, temp, z);
			v3.setPos(x+1, temp, z+1);
			v4.setPos(x,   temp, z+1);
			
			setAmb(1);
			v1.vertAO(map.blockExists(x-1, y1, z), map.blockExists(x, y1, z-1), map.blockExists(x-1, y1, z-1));
			v2.vertAO(map.blockExists(x+1, y1, z), map.blockExists(x, y1, z-1), map.blockExists(x+1, y1, z-1));
			v3.vertAO(map.blockExists(x+1, y1, z), map.blockExists(x, y1, z+1), map.blockExists(x+1, y1, z+1));
			v4.vertAO(map.blockExists(x-1, y1, z), map.blockExists(x, y1, z+1), map.blockExists(x-1, y1, z+1));
			
			final int center = map.getLight(x, y1, z);
			v1.smoothLight(center, map.getLight(x-1, y1, z), map.getLight(x, y1, z-1), map.getLight(x-1, y1, z-1));
			v2.smoothLight(center, map.getLight(x+1, y1, z), map.getLight(x, y1, z-1), map.getLight(x+1, y1, z-1));
			v3.smoothLight(center, map.getLight(x+1, y1, z), map.getLight(x, y1, z+1), map.getLight(x+1, y1, z+1));
			v4.smoothLight(center, map.getLight(x-1, y1, z), map.getLight(x, y1, z+1), map.getLight(x-1, y1, z+1));
			
			rect(textures.bottom);
		}
		if (render1) { // facing Z-
			final int z1 = z-1;
			temp = z;
			
			v1.setPos(x,   y,   temp);
			v2.setPos(x,   y+1, temp);
			v3.setPos(x+1, y+1, temp);
			v4.setPos(x+1, y,   temp);
			
			setAmb(1);
			v1.vertAO(map.blockExists(x, y-1, z1), map.blockExists(x-1, y, z1), map.blockExists(x-1, y-1, z1));
			v2.vertAO(map.blockExists(x, y+1, z1), map.blockExists(x-1, y, z1), map.blockExists(x-1, y+1, z1));
			v3.vertAO(map.blockExists(x, y+1, z1), map.blockExists(x+1, y, z1), map.blockExists(x+1, y+1, z1));
			v4.vertAO(map.blockExists(x, y-1, z1), map.blockExists(x+1, y, z1), map.blockExists(x+1, y-1, z1));
			
			final int center = map.getLight(x, y, z1);
			v1.smoothLight(center, map.getLight(x, y-1, z1), map.getLight(x-1, y, z1), map.getLight(x-1, y-1, z1));
			v2.smoothLight(center, map.getLight(x, y+1, z1), map.getLight(x-1, y, z1), map.getLight(x-1, y+1, z1));
			v3.smoothLight(center, map.getLight(x, y+1, z1), map.getLight(x+1, y, z1), map.getLight(x+1, y+1, z1));
			v4.smoothLight(center, map.getLight(x, y-1, z1), map.getLight(x+1, y, z1), map.getLight(x+1, y-1, z1));

			rect(textures.render1);
		}
		if (render2) { // facing X-
			final int x1 = x-1;
			temp = x;
			
			v1.setPos(temp, y,   z+1);
			v2.setPos(temp, y+1, z+1);
			v3.setPos(temp, y+1, z);
			v4.setPos(temp, y,   z);
			
			setAmb(1);
			v1.vertAO(map.blockExists(x1, y-1, z), map.blockExists(x1, y, z+1), map.blockExists(x1, y-1, z+1));
			v2.vertAO(map.blockExists(x1, y+1, z), map.blockExists(x1, y, z+1), map.blockExists(x1, y+1, z+1));
			v3.vertAO(map.blockExists(x1, y+1, z), map.blockExists(x1, y, z-1), map.blockExists(x1, y+1, z-1));
			v4.vertAO(map.blockExists(x1, y-1, z), map.blockExists(x1, y, z-1), map.blockExists(x1, y-1, z-1));
			
			final int center = map.getLight(x1, y, z);
			v1.smoothLight(center, map.getLight(x1, y-1, z), map.getLight(x1, y, z+1), map.getLight(x1, y-1, z+1));
			v2.smoothLight(center, map.getLight(x1, y+1, z), map.getLight(x1, y, z+1), map.getLight(x1, y+1, z+1));
			v3.smoothLight(center, map.getLight(x1, y+1, z), map.getLight(x1, y, z-1), map.getLight(x1, y+1, z-1));
			v4.smoothLight(center, map.getLight(x1, y-1, z), map.getLight(x1, y, z-1), map.getLight(x1, y-1, z-1));

			rect(textures.render2);
		}
		if (render3) { // facing Z+
			final int z1 = z+1;
			temp = z1;
			
			v1.setPos(x+1, y,   temp);
			v2.setPos(x+1, y+1, temp);
			v3.setPos(x,   y+1, temp);
			v4.setPos(x,   y,   temp);
			
			setAmb(1);
			v1.vertAO(map.blockExists(x, y-1, z1), map.blockExists(x+1, y, z1), map.blockExists(x+1, y-1, z1));
			v2.vertAO(map.blockExists(x, y+1, z1), map.blockExists(x+1, y, z1), map.blockExists(x+1, y+1, z1));
			v3.vertAO(map.blockExists(x, y+1, z1), map.blockExists(x-1, y, z1), map.blockExists(x-1, y+1, z1));
			v4.vertAO(map.blockExists(x, y-1, z1), map.blockExists(x-1, y, z1), map.blockExists(x-1, y-1, z1));
			
			final int center = map.getLight(x, y, z1);
			v1.smoothLight(center, map.getLight(x, y-1, z1), map.getLight(x+1, y, z1), map.getLight(x+1, y-1, z1));
			v2.smoothLight(center, map.getLight(x, y+1, z1), map.getLight(x+1, y, z1), map.getLight(x+1, y+1, z1));
			v3.smoothLight(center, map.getLight(x, y+1, z1), map.getLight(x-1, y, z1), map.getLight(x-1, y+1, z1));
			v4.smoothLight(center, map.getLight(x, y-1, z1), map.getLight(x-1, y, z1), map.getLight(x-1, y-1, z1));

			rect(textures.render3);
		}
		if (render4) { // facing X+
			final int x1 = x+1;
			temp = x1;
			
			v1.setPos(temp, y,   z);
			v2.setPos(temp, y+1, z);
			v3.setPos(temp, y+1, z+1);
			v4.setPos(temp, y,   z+1);
			
			setAmb(1);
			v1.vertAO(map.blockExists(x1, y-1, z), map.blockExists(x1, y, z-1), map.blockExists(x1, y-1, z-1));
			v2.vertAO(map.blockExists(x1, y+1, z), map.blockExists(x1, y, z-1), map.blockExists(x1, y+1, z-1));
			v3.vertAO(map.blockExists(x1, y+1, z), map.blockExists(x1, y, z+1), map.blockExists(x1, y+1, z+1));
			v4.vertAO(map.blockExists(x1, y-1, z), map.blockExists(x1, y, z+1), map.blockExists(x1, y-1, z+1));
			
			final int center = map.getLight(x1, y, z);
			v1.smoothLight(center, map.getLight(x1, y-1, z), map.getLight(x1, y, z-1), map.getLight(x1, y-1, z-1));
			v2.smoothLight(center, map.getLight(x1, y+1, z), map.getLight(x1, y, z-1), map.getLight(x1, y+1, z-1));
			v3.smoothLight(center, map.getLight(x1, y+1, z), map.getLight(x1, y, z+1), map.getLight(x1, y+1, z+1));
			v4.smoothLight(center, map.getLight(x1, y-1, z), map.getLight(x1, y, z+1), map.getLight(x1, y-1, z+1));
			
			rect(textures.render4);
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

		// change from {1/3f, 1/2f, 1/1.5f, 1f} to {1/2.5f, 1/1.85f, 1/1.45f, 1f} it's a bit too strong.
		private static final float[] AMB =
			{1/2.5f, 1/1.85f, 1/1.45f, 1f};

		public void vertAO(boolean side1, boolean side2, boolean corner) {
			if(side1 && side2) {
				ambLit = AMB[0];
				return;
			}
			ambLit = AMB[(side1?0:1)+(side2?0:1)+(corner?0:1)];
		}
		
		public void smoothLight(int center, int side1, int side2, int corner) {
			//int sunLight, sunLightTotal;
			int srcLight, srcLightTotal;
			
			srcLightTotal = LightUtil.getSrcLight(center);
			
			srcLight = LightUtil.getSrcLight(side1);
			srcLightTotal += srcLight == 0 ? center : srcLight;
			
			srcLight = LightUtil.getSrcLight(side2);
			srcLightTotal += srcLight == 0 ? center : srcLight;
			
			srcLight = LightUtil.getSrcLight(corner);
			srcLightTotal += (srcLight == 0 && center != 1) ? center : 
			(srcLight == 1 && center == 1) ? 0 : srcLight; // fix some lighting errors.
			
			// The ">>>2" is a faster version of "/4" and it won't crash if value is 0.
			srcLit = (srcLightTotal>>>2) / 15f;
		}

		public void setPos(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}
