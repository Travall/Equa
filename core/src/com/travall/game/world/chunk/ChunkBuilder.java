package com.travall.game.world.chunk;

import com.badlogic.gdx.graphics.GL20;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.BlockUtils;
import com.travall.game.world.World;

public class ChunkBuilder {
	private final World world;
	private final QuadBuilder opaqeBuilder;
	private final QuadBuilder transBuilder;
	private final CombinedChunk combinedChunk;

	public ChunkBuilder(World world) {
		this.world = world;
		this.combinedChunk = new CombinedChunk();
		this.opaqeBuilder = new QuadBuilder();
		this.transBuilder = new QuadBuilder();
	}

	private final BlockPos position = new BlockPos();

	public CombinedChunk buildChunk(int indexX, int indexY, int indexZ, int chunkSize, ChunkMesh opaqeMesh, ChunkMesh transMesh) {
		final int[][][] data = world.data;
		final BlockPos position = this.position;
		
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
