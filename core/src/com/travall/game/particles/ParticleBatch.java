package com.travall.game.particles;

import static com.travall.game.world.World.world;
import static com.travall.game.utils.BlockUtils.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.glutils.VBO;
import com.travall.game.renderer.vertices.VoxelTerrain;

class ParticleBatch implements Disposable {
	
	// Vertices.
	private final int maxSize;
	private final float[] verts;
	private final VBO vbo;
	private int idx = 0;
	
	// Mathematics.
	private final Vector3 tmp = new Vector3();
	private final Vector3 pos = new Vector3();
	private final Vector3 right = new Vector3();
	private final Vector3 up = new Vector3();
	private final Vector3 down = new Vector3();
	private final Camera cam;
	
	public ParticleBatch(Camera cam) {
		this.cam = cam;
		
		final int size = 1000; // 1000 quad/particles.
		maxSize = size*VoxelTerrain.byteSize; // Max size/length of floats.
		verts = new float[maxSize]; // Allocate the array of floats.
		
		vbo = new VBO(VoxelTerrain.BUFFER, VoxelTerrain.context, GL20.GL_DYNAMIC_DRAW, true);
	}
	
	
	public void begin() {
		VoxelTerrain.begin(cam, world);
		vbo.bind();
		idx = 0;
		
		// Translate facing.
		right.set(cam.direction).crs(cam.up);
		up.set(right).add(cam.up);
		down.set(right).sub(cam.up);
	}
	
	public void end() {
		flush();
		vbo.unbind(true);
		VoxelTerrain.end();
	}
	
	public void flush() {
		if (idx == 0) return;
		vbo.setVertices(verts, 0, idx);
		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, (idx/VoxelTerrain.byteSize)*6, GL20.GL_UNSIGNED_SHORT, 0);
		idx = 0;
	}
	
//  	v3-----v2
//  	|       |
//  	|       |
//  	v4-----v1
	public void draw(final Particle particle) {
		if (idx == maxSize) 
			flush();
		
		final int i = idx;
		final Vector3 pos = particle.getPosition(this.pos);
		final TextureRegion reg = particle.region;
		
		final Vector3 tmp = this.tmp;
		final Vector3 down = this.down;
		final Vector3 up = this.up;
		final float[] verts = this.verts;
		
		final int data =
		world.getData(MathUtils.floor(pos.x), MathUtils.floor(pos.y), MathUtils.floor(pos.z));
		
		final float lights = 
		Float.intBitsToFloat( (((int)(255*(toSunLight(data)/lightScl))<<16) | ((int)(255*(toSrcLight(data)/lightScl))<<8)) | 255);
		
		// v1
		final float size = particle.size;		
		tmp.set(pos).add(down.x*size, down.y*size, down.z*size);
		verts[i]    = tmp.x;
		verts[i+1]  = tmp.y;
		verts[i+2]  = tmp.z;
		verts[i+3]  = lights;
		verts[i+4]  = reg.getU2();
		verts[i+5]  = reg.getV2();

		// v2
		tmp.set(pos).add(up.x*size, up.y*size, up.z*size);
		verts[i+6]   = tmp.x;
		verts[i+7]   = tmp.y;
		verts[i+8]   = tmp.z;
		verts[i+9]   = lights;
		verts[i+10]  = reg.getU2();
		verts[i+11]  = reg.getV();

		// v3
		tmp.set(pos).sub(down.x*size, down.y*size, down.z*size);
		verts[i+12] = tmp.x;
		verts[i+13] = tmp.y;
		verts[i+14] = tmp.z;
		verts[i+15] = lights;
		verts[i+16] = reg.getU();
		verts[i+17] = reg.getV();

		// v4
		tmp.set(pos).sub(up.x*size, up.y*size, up.z*size);
		verts[i+18] = tmp.x;
		verts[i+19] = tmp.y;
		verts[i+20] = tmp.z;
		verts[i+21] = lights;
		verts[i+22] = reg.getU();
		verts[i+23] = reg.getV2();
		this.idx = i + 24;
	}
	
	@Override
	public void dispose() {
		vbo.dispose();
	}
}