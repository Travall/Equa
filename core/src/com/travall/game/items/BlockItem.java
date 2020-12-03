package com.travall.game.items;

import static com.travall.game.utils.BlockUtils.NODATA;
import static com.travall.game.world.World.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.blocks.Block;
import com.travall.game.entities.Player;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.utils.BlockPos;

public class BlockItem {
	public final Block block;
	public final int type;
	
	public BlockItem(Block block) {
		this(block, 0);
	}
	
	public BlockItem(Block block, int type) {
		this.block = block;
		this.type = type;
	}
	
	public void placeBlock(Player player, RayInfo info) {
		block.onPlace(player, info);
		block.setType(info.out, type);
	}
	
	public void setBlock(BlockPos pos) {
		if (world.isOutBound(pos.x, pos.y, pos.z)) return;
		world.data[pos.x][pos.y][pos.z] = (world.data[pos.x][pos.y][pos.z] & NODATA) | block.getID();
		block.setType(pos, type);
	}
	
	public TextureRegion getTexture() {
		return block.getBlockModel().getDefaultTexture(null, type);
	}
	
	@Override
	public int hashCode() {
		return block.hashCode() ^ type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof BlockItem) {
			final BlockItem item = (BlockItem) obj;
			return item.type == this.type && item.block == this.block;
		}
		return false;
	}
}
