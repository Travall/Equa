package com.travall.game.world.chunk;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.graphics.GL20;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.BlockUtils;

public final class ChunkBuilder {
	private static final QuadBuilder opaqeBuilder = new QuadBuilder();
	private static final QuadBuilder transBuilder = new QuadBuilder();
	private static final CombinedChunk combinedChunk = new CombinedChunk();

	private static final BlockPos position = new BlockPos();

	public static CombinedChunk buildChunk(int indexX, int indexY, int indexZ, int chunkSize, ChunkMesh opaqeMesh, ChunkMesh transMesh) {
		final int[][][] data = world.data;
		
		opaqeBuilder.begin();
		transBuilder.begin();
		for (int x = indexX; x < indexX + chunkSize; x++)
		for (int y = indexY; y < indexY + chunkSize; y++)
		for (int z = indexZ; z < indexZ + chunkSize; z++) {
			final int ID = BlockUtils.toBlockID(data[x][y][z]);
			if (ID == 0) continue;
			
			final Block block = BlocksList.get(ID);
			QuadBuilder builder = block.getMaterial().isTransparent() ? transBuilder : opaqeBuilder;
			
			block.getBlockModel().build(builder, position.set(x, y, z));
		}

		combinedChunk.opaque = opaqeMesh == null ? opaqeBuilder.end(GL20.GL_STREAM_DRAW) : opaqeBuilder.end(opaqeMesh);
		combinedChunk.transparent = transMesh == null ? transBuilder.end(GL20.GL_STREAM_DRAW) : transBuilder.end(transMesh);

		return combinedChunk;
	}
}
