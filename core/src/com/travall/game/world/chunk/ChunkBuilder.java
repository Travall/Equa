package com.travall.game.world.chunk;

import com.badlogic.gdx.graphics.GL20;
import com.travall.game.blocks.BlocksList;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.BlockUtils;
import com.travall.game.world.World;

public class ChunkBuilder {
	private final World world;
	private final QuadBuilder builder;

	public ChunkBuilder(World world) {
		this.world = world;
		this.builder = new QuadBuilder();
	}

	private final BlockPos position = new BlockPos();

	public ChunkMesh buildChunk(int indexX, int indexY, int indexZ, int chunkSize, ChunkMesh chunkMesh) {
		final int[][][] data = world.data;
		final BlockPos position = this.position;
		
		builder.begin();
		for (int x = indexX; x < indexX + chunkSize; x++)
		for (int y = indexY; y < indexY + chunkSize; y++)
		for (int z = indexZ; z < indexZ + chunkSize; z++) {
			final int ID = BlockUtils.toBlockID(data[x][y][z]);
			if (ID == 0) continue;
			BlocksList.get(ID).getBlockModel().build(builder, position.set(x, y, z));
		}

		return chunkMesh == null ? builder.end(GL20.GL_STREAM_DRAW) : builder.end(chunkMesh);
	}
}
