package com.travall.game.renderer.quad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.glutils.QuadIndexBuffer;
import com.travall.game.renderer.vertices.VertInfo;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.world.chunk.ChunkMesh;

public class QuadBuilder extends QuadInfo {

	private static final int maxFloats = QuadIndexBuffer.maxVertex*VoxelTerrain.floatSize;

	private final FloatArray vertices = new FloatArray(256) {
		protected float[] resize (int newSize) {
			if (items.length == maxFloats) throw new IllegalStateException("Max vertex size has been reached!");
			return super.resize(Math.min(newSize, maxFloats));
		}
	};
	
	public final Vector3
	p1 = new Vector3(),
	p2 = new Vector3(),
	p3 = new Vector3(),
	p4 = new Vector3();

//     v3-----v2
//     |       |
//     |       |
//     v4-----v1
	public void rect() {
		rect(this);
	}
	
	public void rect(final TextureRegion region) {
		vertices.add(v1.x, v1.y, v1.z, v1.packData());
		vertices.add(region.getU2(), region.getV2());

		vertices.add(v2.x, v2.y, v2.z, v2.packData());
		vertices.add(region.getU2(), region.getV());

		vertices.add(v3.x, v3.y, v3.z, v3.packData());
		vertices.add(region.getU(), region.getV());

		vertices.add(v4.x, v4.y, v4.z, v4.packData());
		vertices.add(region.getU(), region.getV2());
	}
	
	public void rect(final QuadInfo quad) {
		final VertInfo v1 = quad.v1;
		final VertInfo v2 = quad.v2;
		final VertInfo v3 = quad.v3;
		final VertInfo v4 = quad.v4;
		final TextureRegion region = quad.region;
		
		vertices.add(v1.x, v1.y, v1.z, v1.packData());
		vertices.add(region.getU2(), region.getV2());

		vertices.add(v2.x, v2.y, v2.z, v2.packData());
		vertices.add(region.getU2(), region.getV());

		vertices.add(v3.x, v3.y, v3.z, v3.packData());
		vertices.add(region.getU(), region.getV());

		vertices.add(v4.x, v4.y, v4.z, v4.packData());
		vertices.add(region.getU(), region.getV2());
	}
	
	public void rect(final QuadNode node) {
		final TextureRegion region = node.region;
		
		vertices.add(p1.x, p1.y, p1.z, node.v1.packData());
		vertices.add(region.getU2(), region.getV2());

		vertices.add(p2.x, p2.y, p2.z, node.v2.packData());
		vertices.add(region.getU2(), region.getV());

		vertices.add(p3.x, p3.y, p3.z, node.v3.packData());
		vertices.add(region.getU(), region.getV());

		vertices.add(p4.x, p4.y, p4.z, node.v4.packData());
		vertices.add(region.getU(), region.getV2());
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
}