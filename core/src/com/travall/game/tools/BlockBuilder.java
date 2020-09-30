package com.travall.game.tools;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.blocks.Block;
import com.travall.game.world.World;
import com.travall.game.glutils.QuadIndexBuffer;

public class BlockBuilder {
	
	private static final int maxFloats = QuadIndexBuffer.maxVertex*VoxelTerrain.floatSize;
	
	public final VertInfo v1, v2, v3, v4;
	
	public final GridPoint3
	side1  = new GridPoint3(),
	side2  = new GridPoint3(),
	corner = new GridPoint3();
	
	public final CachePoint3 center;
	
	private final FloatArray vertices = new FloatArray(256) {
		protected float[] resize (int newSize) {
			if (items.length == maxFloats) throw new IllegalStateException("Max vertex size has been reached!");
			return super.resize(Math.min(newSize, maxFloats));
		}
	};
	
<<<<<<< HEAD
	private final World map;
	
	public BlockBuilder(World map) {
=======
	private final MapGenerator map;
	private final short[][][] blocks;
	private final byte[][][] lights;
	
	public BlockBuilder(MapGenerator map, byte[][][] lights) {
>>>>>>> 3351400c47121fb6eea25ab725f7f03992b11086
		this.map = map;
		this.blocks = map.blocks;
		this.lights = lights;
		this.center = new CachePoint3(map);
		v1 = new VertInfo(this, center);
		v2 = new VertInfo(this, center);
		v3 = new VertInfo(this, center);
		v4 = new VertInfo(this, center);
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
		setBlock(block);
		
		if (renderTop) { // facing Y+
			final int y1 = y+1;
			temp = y1;
			
			v1.setPos(x+1, temp, z);
			v2.setPos(x,   temp, z);
			v3.setPos(x,   temp, z+1);
			v4.setPos(x+1, temp, z+1);
			
			setAmb(1f);
			center.set(x, y1, z);
			v1.calcLight(side1.set(x+1, y1, z), side2.set(x, y1, z-1), corner.set(x+1, y1, z-1));
			v2.calcLight(side1.set(x-1, y1, z), side2.set(x, y1, z-1), corner.set(x-1, y1, z-1));
			v3.calcLight(side1.set(x-1, y1, z), side2.set(x, y1, z+1), corner.set(x-1, y1, z+1));
			v4.calcLight(side1.set(x+1, y1, z), side2.set(x, y1, z+1), corner.set(x+1, y1, z+1));

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
			center.set(x, y1, z);
			v1.calcLight(side1.set(x-1, y1, z), side2.set(x, y1, z-1), corner.set(x-1, y1, z-1));
			v2.calcLight(side1.set(x+1, y1, z), side2.set(x, y1, z-1), corner.set(x+1, y1, z-1));
			v3.calcLight(side1.set(x+1, y1, z), side2.set(x, y1, z+1), corner.set(x+1, y1, z+1));
			v4.calcLight(side1.set(x-1, y1, z), side2.set(x, y1, z+1), corner.set(x-1, y1, z+1));
			
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
			center.set(x, y, z1);
			v1.calcLight(side1.set(x, y-1, z1), side2.set(x-1, y, z1), corner.set(x-1, y-1, z1));
			v2.calcLight(side1.set(x, y+1, z1), side2.set(x-1, y, z1), corner.set(x-1, y+1, z1));
			v3.calcLight(side1.set(x, y+1, z1), side2.set(x+1, y, z1), corner.set(x+1, y+1, z1));
			v4.calcLight(side1.set(x, y-1, z1), side2.set(x+1, y, z1), corner.set(x+1, y-1, z1));

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
			center.set(x1, y, z);
			v1.calcLight(side1.set(x1, y-1, z), side2.set(x1, y, z+1), corner.set(x1, y-1, z+1));
			v2.calcLight(side1.set(x1, y+1, z), side2.set(x1, y, z+1), corner.set(x1, y+1, z+1));
			v3.calcLight(side1.set(x1, y+1, z), side2.set(x1, y, z-1), corner.set(x1, y+1, z-1));
			v4.calcLight(side1.set(x1, y-1, z), side2.set(x1, y, z-1), corner.set(x1, y-1, z-1));

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
			center.set(x, y, z1);
			v1.calcLight(side1.set(x, y-1, z1), side2.set(x+1, y, z1), corner.set(x+1, y-1, z1));
			v2.calcLight(side1.set(x, y+1, z1), side2.set(x+1, y, z1), corner.set(x+1, y+1, z1));
			v3.calcLight(side1.set(x, y+1, z1), side2.set(x-1, y, z1), corner.set(x-1, y+1, z1));
			v4.calcLight(side1.set(x, y-1, z1), side2.set(x-1, y, z1), corner.set(x-1, y-1, z1));

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
			center.set(x1, y, z);
			v1.calcLight(side1.set(x1, y-1, z), side2.set(x1, y, z-1), corner.set(x1, y-1, z-1));
			v2.calcLight(side1.set(x1, y+1, z), side2.set(x1, y, z-1), corner.set(x1, y+1, z-1));
			v3.calcLight(side1.set(x1, y+1, z), side2.set(x1, y, z+1), corner.set(x1, y+1, z+1));
			v4.calcLight(side1.set(x1, y-1, z), side2.set(x1, y, z+1), corner.set(x1, y-1, z+1));
			
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
	
	private void setBlock(Block block) {
		v1.block = block;
		v2.block = block;
		v3.block = block;
		v4.block = block;
	}
	
	public static class VertInfo {
		/** Positions */
		public float x, y, z;
		/** Lighting. Must clamp it to 0 to 1 if necessary. Use the <code>MathUtils.clamp(value, 0f, 1f)</code> */
		public float ambLit = 1f, srcLit, sunLit = 1f; 
//		/** Texture coordinates */
//		public float u, v; // Not needed.
		
		private final BlockBuilder map;
		/** Cache boolean of "is two sides" This boolean is for fix light leakage. */
		private boolean twoSides;
		/** Cache the current block. */
		Block block;
		/** Cache the current face's position. */
		private final CachePoint3 center;
		
		VertInfo (BlockBuilder map, CachePoint3 center) {
			this.map = map;
			this.center = center;
		}
		
		public float packData() {
			return Float.intBitsToFloat((((int)(255*sunLit)<<16)|((int)(255*srcLit)<<8)|((int)(255*ambLit))));
		}

		// change from {1/3f, 1/2f, 1/1.5f, 1f} to {1/2.2f, 1/1.7f, 1/1.4f, 1f} it's a bit too strong.
		private static final float[] AMB =
			{1/2.2f, 1/1.7f, 1/1.4f, 1f};

		private void vertAO(boolean side1, boolean side2, GridPoint3 corner) {
			if(side1 && side2) {
				ambLit = AMB[0];
				return;
			} 
			ambLit = AMB[Math.min((side1?0:1)+(side2?0:1)+(map.lightExists(corner)?0:1), map.blockExists(center)?2:3)];
		}
		
		private void smoothLight(int center, int side1, int side2, int corner) {
			//int sunLight, sunLightTotal;
			int light;
			int srcLightTotal, srcLightCount = 1;
			
			final int srcCenter = LightUtil.getSrcLight(center);
			srcLightTotal = srcCenter;
			
			light = LightUtil.getSrcLight(side1);
			if (light != 0) {
				srcLightCount++;
				srcLightTotal += light;
			}
			
			light = LightUtil.getSrcLight(side2);
			if (light != 0) {
				srcLightCount++;
				srcLightTotal += light;
			}
			
			light = LightUtil.getSrcLight(corner);
			if (!twoSides && (light != 0 || srcCenter == 1)) {
				srcLightCount++;
				srcLightTotal += (light == 1 && srcCenter == 1) ? 0 : light;
			}
			
			srcLit = srcLightCount == 1 ? srcLightTotal/LightUtil.lightScl : (srcLightTotal/srcLightCount)/LightUtil.lightScl;
		}
		
		void calcLight(GridPoint3 side1, GridPoint3 side2, GridPoint3 corner) {
			if (!block.isSrclight()) vertAO(map.lightExists(side1), map.lightExists(side2), corner);
			twoSides = map.lightExists(side1) && map.lightExists(side2);
			smoothLight(center.light, map.getLight(side1), map.getLight(side2), map.getLight(corner));
		}

		public void setPos(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	private static class CachePoint3 extends GridPoint3 {
		/** */
		private static final long serialVersionUID = 2869339484507704115L;
		
		public int light;
		public boolean blockExists;
		
		public final MapGenerator map;
		
		public CachePoint3(MapGenerator map) {
			this.map = map;
		}
		
		public CachePoint3 set(int x, int y, int z) {
			light = map.getLight(x, y, z);
			blockExists = map.blockExists(x, y, z);
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}
	}

	private boolean blockExists(final GridPoint3 pos) {
        return !isOutBound(pos) && blocks[pos.x][pos.y][pos.z] != 0;
    }
	
	private boolean lightExists(final GridPoint3 pos) {
		return !isOutBound(pos) && blocks[pos.x][pos.y][pos.z] != 0 && LightUtil.getSrcLight(lights[pos.x][pos.y][pos.z]) == 0;
    }
    
	private boolean isOutBound(final GridPoint3 pos) {
    	return pos.x < 0 || pos.y < 0 || pos.z < 0 || pos.x >= map.mapWidth || pos.y >= map.mapHeight || pos.z >= map.mapLength;
    }
    
	private int getLight(final GridPoint3 pos) {
 	    return isOutBound(pos) ? 0xF0 : lights[pos.x][pos.y][pos.z]&0xFF;
 	}
}
