package com.travall.game.blocks;

import static com.travall.game.world.World.world;

import com.travall.game.blocks.data.FaceComponent;
import com.travall.game.blocks.data.KeyHolder;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.TorchModel;
import com.travall.game.entities.Player;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.renderer.block.BlockTextures;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class Torch extends Block {
	
	public final FaceComponent face = new FaceComponent();

	public Torch(int blockID) {
		super(blockID);
		this.model = new TorchModel(this, new BlockTextures(UltimateTexture.createRegion(14, 2),UltimateTexture.createRegion(14, 1)));
		this.material = Material.TORCH;
		this.lightLevel = 15;
		
		this.manager.addCompoment(KeyHolder.FACING, face);
	}
	
	@Override
	public boolean onPlace(Player player, RayInfo rayInfo) {
		final BlockPos out = rayInfo.out;
		if (rayInfo.face != Facing.DOWN && world.getBlock(rayInfo.in).isFaceSolid(rayInfo.in, rayInfo.face)) {
			
			if (super.onPlace(player, rayInfo)) {
				face.setFace(out, rayInfo.face);
			}
			
		} else if (super.onPlace(player, rayInfo)) {
			
			BlockPos offset = out.offset(Facing.DOWN);
			if (world.getBlock(offset).isFaceSolid(offset, Facing.UP)) {
				face.setFace(out, Facing.UP);
				return true;
			}
			
			offset = out.offset(Facing.NORTH);
			if (world.getBlock(offset).isFaceSolid(offset, Facing.SOUTH)) {
				face.setFace(out, Facing.SOUTH);
				return true;
			}
			
			offset = out.offset(Facing.EAST);
			if (world.getBlock(offset).isFaceSolid(offset, Facing.WEST)) {
				face.setFace(out, Facing.WEST);
				return true;
			}
			
			offset = out.offset(Facing.SOUTH);
			if (world.getBlock(offset).isFaceSolid(offset, Facing.NORTH)) {
				face.setFace(out, Facing.NORTH);
				return true;
			}
			
			offset = out.offset(Facing.WEST);
			if (world.getBlock(offset).isFaceSolid(offset, Facing.EAST)) {
				face.setFace(out, Facing.EAST);
				return true;
			}
		}
		
		return false;
	}
}
