package com.travall.game.world.chunk;

import static com.travall.game.world.World.world;
import static com.travall.game.world.World.chunkSize;

import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.renderer.chunk.ChunkMesh;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.BlockUtils;

public final class ChunkBuilder {
	private static final QuadBuilder opaqeBuilder = new QuadBuilder();
	private static final QuadBuilder transBuilder = new QuadBuilder();

	private static final BlockPos position = new BlockPos();

	public static ChunkMesh buildChunk(int indexX, int indexY, int indexZ, ChunkMesh mesh) {
		final int[][][] data = world.data;
		
		indexX *= chunkSize;
		indexY *= chunkSize;
		indexZ *= chunkSize;
		
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

		if (mesh == null) {
			return new ChunkMesh(opaqeBuilder.end(), transBuilder.end());
		}
		
		mesh.isDirty = false;
		opaqeBuilder.end(mesh.opaqeVBO);
		transBuilder.end(mesh.transVBO);
		return mesh;
	}
}
