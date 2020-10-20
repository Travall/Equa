package com.travall.game.blocks.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.Log;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.quad.QuadBuilder;
import com.travall.game.renderer.quad.QuadNode;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;
import com.travall.game.utils.LogTypes;

public class LogModel implements IBlockModel {

	private final TextureRegion texture;
	private final Log log;

	private final QuadNode quad1, quad2, quad3, quad4, quad5, quad6;

	public LogModel(Log log, BlockTextures textures) {
		this.texture = textures.north;
		this.log = log;
		
		quad1 = new QuadNode();
		quad1.p1.set(1, 1, 0);
		quad1.p2.set(0, 1, 0);
		quad1.p3.set(0, 1, 1);
		quad1.p4.set(1, 1, 1);
		quad1.face = Facing.UP;
		quad1.region.setRegion(textures.top);
		
		quad2 = new QuadNode();
		quad2.p1.set(0, 0, 0);
		quad2.p2.set(1, 0, 0);
		quad2.p3.set(1, 0, 1);
		quad2.p4.set(0, 0, 1);
		quad2.face = Facing.DOWN;
		quad2.region.setRegion(textures.bottom);
		
		quad3 = new QuadNode();
		quad3.p1.set(1, 0, 1);
		quad3.p2.set(1, 1, 1);
		quad3.p3.set(0, 1, 1);
		quad3.p4.set(0, 0, 1);
		quad3.face = Facing.NORTH;
		quad3.region.setRegion(textures.south);
		
		quad4 = new QuadNode();
		quad4.p1.set(1, 0, 0);
		quad4.p2.set(1, 1, 0);
		quad4.p3.set(1, 1, 1);
		quad4.p4.set(1, 0, 1);
		quad4.face = Facing.EAST;
		quad4.region.setRegion(textures.south);
		
		quad5 = new QuadNode();
		quad5.p1.set(0, 0, 0);
		quad5.p2.set(0, 1, 0);
		quad5.p3.set(1, 1, 0);
		quad5.p4.set(1, 0, 0);
		quad5.face = Facing.SOUTH;
		quad5.region.setRegion(textures.south);
		
		quad6 = new QuadNode();
		quad6.p1.set(0, 0, 1);
		quad6.p2.set(0, 1, 1);
		quad6.p3.set(0, 1, 0);
		quad6.p4.set(0, 0, 0);
		quad6.face = Facing.WEST;
		quad6.region.setRegion(textures.west);
	}
	
	private final BlockPos second = new BlockPos();

	private void setTexture(BlockTextures blockTextures) {
		quad1.region.setRegion(blockTextures.top);
		quad2.region.setRegion(blockTextures.bottom);
		quad3.region.setRegion(blockTextures.south);
		quad4.region.setRegion(blockTextures.south);
		quad5.region.setRegion(blockTextures.south);
		quad6.region.setRegion(blockTextures.west);
	}

	@Override
	public void build(QuadBuilder builder, BlockPos position) {
		int x = position.x, y = position.y, z = position.z;
		final Block block = this.log;

		LogTypes logType = log.type.getType(position);

		switch (logType) {
			case OAK: setTexture(LogTypes.OAK.textures); break;
			case DARKOAK: setTexture(LogTypes.DARKOAK.textures); break;
		}

		if (block.canAddFace(position, second.set(x, y+1, z), Facing.UP))    quad1.rect(builder, position);
		if (block.canAddFace(position, second.set(x, y-1, z), Facing.DOWN))  quad2.rect(builder, position);
		if (block.canAddFace(position, second.set(x, y, z+1), Facing.NORTH)) quad3.rect(builder, position);
		if (block.canAddFace(position, second.set(x+1, y, z), Facing.EAST))  quad4.rect(builder, position);
		if (block.canAddFace(position, second.set(x, y, z-1), Facing.SOUTH)) quad5.rect(builder, position);
		if (block.canAddFace(position, second.set(x-1, y, z), Facing.WEST))  quad6.rect(builder, position);
	}

	@Override
	public TextureRegion getDefaultTexture() {
		return texture;
	}
}
