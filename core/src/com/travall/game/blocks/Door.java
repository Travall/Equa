package com.travall.game.blocks;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.Input.Buttons;
import com.travall.game.blocks.data.DoorComponent;
import com.travall.game.blocks.data.HorizontalComponent;
import com.travall.game.blocks.materials.Material;
import com.travall.game.blocks.models.DoorModel;
import com.travall.game.entities.Player;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.ui.actors.BlockSeletion;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;
import com.travall.game.world.World;

public class Door extends Block {
	
	public final DoorComponent doorComponent = new DoorComponent();
	public final HorizontalComponent horizontalComponent = new HorizontalComponent();

	protected Door(int blockID) {
		super(blockID);
		this.material = Material.DOOR;
		this.model = new DoorModel(this);
		this.manager.addCompoment(doorComponent);
		this.manager.addCompoment(horizontalComponent);
		BlockSeletion.add(this);
	}
	
	@Override
	public boolean onClick(Player player, RayInfo rayInfo, int button) {
		if (button != Buttons.RIGHT) return false;
		final BlockPos pos = rayInfo.in;
		
		if (isUpper(pos)) {
			final BlockPos offset = pos.offset(Facing.DOWN);
			if (world.getBlock(offset) == this) {
				doorComponent.setIsOpen(pos, !isOpen(pos));
				doorComponent.setIsOpen(offset, !isOpen(offset));
				world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
				return true;
			}
		} else {
			final BlockPos offset = pos.offset(Facing.UP);
			if (world.getBlock(offset) == this) {
				doorComponent.setIsOpen(pos, !isOpen(pos));
				doorComponent.setIsOpen(offset, !isOpen(offset));
				world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean onPlace(Player player, RayInfo rayInfo) {
		final BlockPos out = rayInfo.out;
		if (!world.isAirBlock(out.x, out.y, out.z)) return false;
		final BlockPos up = out.offset(Facing.UP);
		if (!world.isAirBlock(up.x, up.y, up.z) || World.mapHeight <= up.y) return false;
		
		onPlace(out);
		onPlace(up);
		
		doorComponent.setIsUpper(up, true);
		
		Facing face = rayInfo.face2;
		switch (face) {
		case EAST:
		case SOUTH:
		case WEST: break;
		default: face = Facing.NORTH; break;
		}
		
		horizontalComponent.setFace(out, face);
		horizontalComponent.setFace(up, face);
		
		return true;
	}
	
	@Override
	public boolean onPlace(BlockPos pos) {
		world.setBlock(pos.x, pos.y, pos.z, this);		
		world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
		return true;
	}
	
	@Override
	public boolean onDestroy(BlockPos pos) {
		if (isUpper(pos)) {
			BlockPos offset = pos.offset(Facing.DOWN);
			if (world.getBlock(offset) == this) {
				world.setBlock(offset.x, offset.y, offset.z, BlocksList.AIR);
				this.onDestroy(offset);
			}
		} else {
			BlockPos offset = pos.offset(Facing.UP);
			if (world.getBlock(offset) == this) {
				world.setBlock(offset.x, offset.y, offset.z, BlocksList.AIR);
				this.onDestroy(offset);
			}
		}
		
		world.setBlock(pos.x, pos.y, pos.z, BlocksList.AIR);
		world.setMeshDirtyShellAt(pos.x, pos.y, pos.z);
		return true;
	}
	
	public boolean isUpper(BlockPos pos) {
		return doorComponent.isUpper(pos);
	}
	
	public boolean isOpen(BlockPos pos) {
		return doorComponent.isOpen(pos);
	}
}
