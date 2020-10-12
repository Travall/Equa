package com.travall.game.blocks.models;

import com.badlogic.gdx.utils.Array;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;

public abstract class BlockModelNodes implements IBlockModel {
	
	/** A full static model of this block. */
	protected final Array<QuadNode> quads;
	/** A list of nodes adding to the mesh. */
	protected final Array<QuadNode> nodes;
	
	public BlockModelNodes(int size) {
		quads = new Array<>(size);
		nodes = new Array<>(size);
	}
	
	public void build(QuadBuilder builder, BlockPos position) {
		for (final QuadNode node : nodes) {
			node.rect(builder, position);
		}
		clearNodes();
	}
	
	protected final void addNodeIndex(int index) {
		nodes.add(quads.get(index));
	}
	
	protected final void clearNodes() {
		nodes.size = 0;
	}
}
