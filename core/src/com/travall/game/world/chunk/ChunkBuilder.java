package com.travall.game.world.chunk;

import com.badlogic.gdx.graphics.GL20;
import com.travall.game.blocks.Air;
import com.travall.game.blocks.Block;
import com.travall.game.renderer.block.BlockBuilder;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;
import com.travall.game.world.World;

public class ChunkBuilder {
	private final World world;
	private final BlockBuilder builder;

	public ChunkBuilder(World world) {
		this.world = world;
		this.builder = new BlockBuilder(world);
	}

	private final BlockPos primaray = new BlockPos();
	private final BlockPos secondary = new BlockPos();

	public ChunkMesh buildChunk(int indexX, int indexY, int indexZ, int chunkSize, ChunkMesh chunkMesh) {
		builder.begin();
		final short[][][] blocks = world.blocks;
		for (int x = indexX; x < indexX + chunkSize; x++) {
			for (int y = indexY; y < indexY + chunkSize; y++) {
				for (int z = indexZ; z < indexZ + chunkSize; z++) {
					if (blocks[x][y][z] == Air.id) continue;
					
					primaray.set(x, y, z);
					Block block = world.getBlock(primaray);		

					boolean up =    block.canAddFace(primaray, secondary.set(x, y+1, z), Facing.UP);
					boolean down =  block.canAddFace(primaray, secondary.set(x, y-1, z), Facing.DOWN);
					boolean south = block.canAddFace(primaray, secondary.set(x, y, z-1), Facing.SOUTH);
					boolean west =  block.canAddFace(primaray, secondary.set(x-1, y, z), Facing.WEST);
					boolean north = block.canAddFace(primaray, secondary.set(x, y, z+1), Facing.NORTH);
					boolean east =  block.canAddFace(primaray, secondary.set(x+1, y, z), Facing.SOUTH);

					builder.buildCube(block, primaray, up, down, south, west, north, east);
				}
			}
		}

		return chunkMesh == null ? builder.end(GL20.GL_STREAM_DRAW) : builder.end(chunkMesh);
	}
}
