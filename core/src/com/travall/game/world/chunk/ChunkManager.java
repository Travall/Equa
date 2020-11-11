package com.travall.game.world.chunk;

import static com.badlogic.gdx.math.MathUtils.floor;
import static com.travall.game.world.World.*;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.chunk.ChunkMesh;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.math.ChunkPlane;

public class ChunkManager implements Disposable {
	private final ChunkMesh[][][] meshes = new ChunkMesh[xChunks][yChunks][zChunks];
	
	private static final ChunkPlane[] planes = new ChunkPlane[4];
	private static final BlockPos blockPos = new BlockPos();
	private static final Array<ChunkMesh> trans = new Array<>(128);
	private static final int haft = chunkSize / 2;
	
	private boolean hasInts;
	
	public void intsMeshes() {
		if (hasInts) return;
		for (int x = 0; x < xChunks; x++)
		for (int y = 0; y < yChunks; y++)
		for (int z = 0; z < zChunks; z++) {
			meshes[x][y][z] = ChunkBuilder.buildChunk(x, y, z, null);
		}
		hasInts = true;
	}
	
	public void render(Camera camera) {
		final Plane[] tmpPlanes =  camera.frustum.planes;
		for (int i = 2; i < tmpPlanes.length; i++) {
			planes[i-2].set(tmpPlanes[i]);
		}

		UltimateTexture.texture.bind();
		VoxelTerrain.begin(camera);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		trans.size = 0;
		for(int x = 0; x < xChunks; x++)
		for(int y = 0; y < yChunks; y++)
		for(int z = 0; z < zChunks; z++) {
			final ChunkMesh mesh = meshes[x][y][z];
			if (mesh.isDirty) {
				ChunkBuilder.buildChunk(x, y, z, mesh);
			}

			final float xPos, yPos, zPos;
			xPos = (x << chunkShift) + haft;
			yPos = (y << chunkShift) + haft;
			zPos = (z << chunkShift) + haft;
			
			int isVisable = -1;
			if(!mesh.opaqeVBO.isEmpty && (isVisable = mesh.isVisable(planes, xPos, yPos, zPos)?1:0) == 1) {
				mesh.opaqeVBO.render();
			}
			
			if (!mesh.transVBO.isEmpty && (isVisable == 1 || (isVisable == -1 && mesh.isVisable(planes, xPos, yPos, zPos)))) {
				trans.add(mesh);
			}
		}
		Gdx.gl30.glBindVertexArray(0);
		
		if (trans.notEmpty()) {
			if (world.getBlock(blockPos.set(floor(camera.position.x), floor(camera.position.y), floor(camera.position.z))).getMaterial().isTransparent())
				Gdx.gl.glDisable(GL20.GL_CULL_FACE);

			Gdx.gl.glEnable(GL20.GL_BLEND);
			for (ChunkMesh mesh : trans) {
				mesh.transVBO.render();
			}
			Gdx.gl30.glBindVertexArray(0);
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		VoxelTerrain.end();
	}
	
	public void setDirtyCoordinate(int x, int y, int z) {
		setDirtyIndex(x >> chunkShift, y >> chunkShift, z >> chunkShift);
	}
	
	public void setDirtyIndex(int x, int y, int z) {
		if (x < 0 || x >= xChunks || y < 0 || y >= yChunks || z < 0 || z >= zChunks)
			return;
		
		meshes[x][y][z].isDirty = true;
	}
	
	@Override
	public void dispose() {
		if (!hasInts) return;
		
		Arrays.fill(trans.items, null);
		for (int x = 0; x < xChunks; x++)
		for (int y = 0; y < yChunks; y++)
		for (int z = 0; z < zChunks; z++) {
			meshes[x][y][z].dispose();
		}
		
		hasInts = false;
	}
	
	static {
		for (int i = 0; i < planes.length; i++) {
			planes[i] = new ChunkPlane();
		}
	}
}
