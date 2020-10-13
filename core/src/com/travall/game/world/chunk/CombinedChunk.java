package com.travall.game.world.chunk;

public class CombinedChunk {
	public ChunkMesh opaque;
	public ChunkMesh transparent;

	public CombinedChunk(ChunkMesh opaque, ChunkMesh transparent) {
		this.opaque = opaque;
		this.transparent = transparent;
	}

	public CombinedChunk() {
	}
}
