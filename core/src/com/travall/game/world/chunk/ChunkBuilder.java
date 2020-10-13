package com.travall.game.world.chunk;

import com.badlogic.gdx.graphics.GL20;
import com.travall.game.blocks.BlocksList;
import com.travall.game.blocks.materials.Material;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.BlockUtils;
import com.travall.game.world.World;

public class ChunkBuilder {
	private final World world;
	private final QuadBuilder opaqueBuilder;
	private final QuadBuilder transparentBuilder;
	private final CombinedChunk combinedChunk;

	public ChunkBuilder(World world) {
		this.world = world;
		this.combinedChunk = new CombinedChunk();
		this.opaqueBuilder = new QuadBuilder();
		this.transparentBuilder = new QuadBuilder();

	}

	private final BlockPos position = new BlockPos();

	public CombinedChunk buildChunk(int indexX, int indexY, int indexZ, int chunkSize, ChunkMesh opaqueChunkMesh, ChunkMesh transparentChunkMesh) {
		final int[][][] data = world.data;
		final BlockPos position = this.position;
		
		opaqueBuilder.begin();
		transparentBuilder.begin();
		for (int x = indexX; x < indexX + chunkSize; x++)
		for (int y = indexY; y < indexY + chunkSize; y++)
		for (int z = indexZ; z < indexZ + chunkSize; z++) {
			final int ID = BlockUtils.toBlockID(data[x][y][z]);
			if (ID == 0) continue;
			if(BlocksList.get(ID).getMaterial().getBlendType() == Material.BlendType.SOLID) BlocksList.get(ID).getBlockModel().build(opaqueBuilder, position.set(x, y, z));
			else if(BlocksList.get(ID).getMaterial().getBlendType() == Material.BlendType.TRANS || BlocksList.get(ID).getMaterial().getBlendType() == Material.BlendType.CLEAR) BlocksList.get(ID).getBlockModel().build(transparentBuilder, position.set(x, y, z));
		}

		combinedChunk.opaque = opaqueChunkMesh == null ? opaqueBuilder.end(GL20.GL_STREAM_DRAW) : opaqueBuilder.end(opaqueChunkMesh);
		combinedChunk.transparent = transparentChunkMesh == null ? transparentBuilder.end(GL20.GL_STREAM_DRAW) : transparentBuilder.end(transparentChunkMesh);

		return combinedChunk;
	}
}
