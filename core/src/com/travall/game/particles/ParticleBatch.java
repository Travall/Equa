package com.travall.game.particles;

import static com.travall.game.world.World.world;
import static com.travall.game.utils.BlockUtils.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.glutils.VBO;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.utils.BlockUtils;

class ParticleBatch implements Disposable {
	
	// Vertices.
	private final int maxSize;
	private final float[] verts;
	private final VBO vbo;
	private int idx = 0;
	
	// Mathematics.
	private final Vector3 tmp = new Vector3();
	private final Vector3 pos = new Vector3();
	private final Matrix4 mat = new Matrix4();
	private final Camera cam;
	
	public ParticleBatch(Camera cam) {
		this.cam = cam;
		
		final int size = 1000; // 1000 quad/particles.
		maxSize = size*VoxelTerrain.byteSize; // Max size/length of floats.
		verts = new float[maxSize]; // Allocate the array of floats.
		
		vbo = new VBO(VoxelTerrain.BUFFER, VoxelTerrain.context, GL20.GL_DYNAMIC_DRAW, true);
	}
	
	
	public void begin() {
		VoxelTerrain.begin(cam);
		vbo.bind();
		idx = 0;
	}
	
	public void end() {
		flush();
		vbo.unbind();
		VoxelTerrain.end();
	}
	
	public void flush() {
		if (idx == 0) return;
		vbo.setVertices(verts, 0, idx);
		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, (idx/VoxelTerrain.byteSize)*6, GL20.GL_UNSIGNED_SHORT, 0);
		idx = 0;
	}
	
	public void draw(final Particle particle) {
		if (idx == maxSize) 
			flush();
		
		final int i = idx;
		final Vector3 pos = particle.getPosition(this.pos);
		mat.setToTranslation(pos); 
		mat.scl(0.08f);
		mat.rotateTowardDirection(tmp.set(cam.position).sub(pos), cam.up); // Face the particle toward the camera.
		
		final TextureRegion reg = particle.region;
		
		final int data = 
		world.getData(MathUtils.floor(pos.x), MathUtils.floor(pos.y), MathUtils.floor(pos.z));
		
		final float lights = 
		Float.intBitsToFloat( (((int)(255*(toSunLight(data)/lightScl))<<16) | ((int)(255*(toSrcLight(data)/lightScl))<<8)) | 255);
		
		final float[] verts = this.verts;
		
		pos.set(-1f, -1f, 0f).mul(mat);
		verts[i]    = pos.x;
		verts[i+1]  = pos.y;
		verts[i+2]  = pos.z;
		verts[i+3]  = lights;
		verts[i+4]  = reg.getU2();
		verts[i+5]  = reg.getV2();

		pos.set(-1f, 1f, 0f).mul(mat);
		verts[i+6]   = pos.x;
		verts[i+7]   = pos.y;
		verts[i+8]   = pos.z;
		verts[i+9]   = lights;
		verts[i+10]  = reg.getU2();
		verts[i+11]  = reg.getV();

		pos.set(1f, 1f, 0f).mul(mat);
		verts[i+12] = pos.x;
		verts[i+13] = pos.y;
		verts[i+14] = pos.z;
		verts[i+15] = lights;
		verts[i+16] = reg.getU();
		verts[i+17] = reg.getV();

		pos.set(1f, -1f, 0f).mul(mat);
		verts[i+18] = pos.x;
		verts[i+19] = pos.y;
		verts[i+20] = pos.z;
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