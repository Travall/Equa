package com.travall.game.renderer.quad;

import static com.travall.game.world.World.world;
import static com.travall.game.utils.Utils.gamma;

import com.travall.game.blocks.Block;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Facing;

public class QuadNode extends QuadInfo {
	
	public static final float 
	lightHigh = 1.0f, // old: 1.0f  new: 1.0f
	lightMed = gamma(0.95), // old: 0.82f new: 0.86f
	lightLow = gamma(0.9), // old: 0.68f new: 0.75f
	lightDim = gamma(0.85); // old: 0.6f  new: 0.65f
	
	public Facing face;
	
	public boolean isInside;
	
	private final BlockPos
	center = new BlockPos(),
	side1  = new BlockPos(),
	side2  = new BlockPos(),
	corner = new BlockPos();

	public void rect(QuadBuilder builder, BlockPos pos) {
		final int x = pos.x, y = pos.y, z = pos.z;
		final Block block = world.getBlock(pos);
		
		int x1, y1, z1;
		
		final float xf = x, yf = y, zf = z; 
		builder.p1.set(v1.x+xf, v1.y+yf, v1.z+zf);
		builder.p2.set(v2.x+xf, v2.y+yf, v2.z+zf);
		builder.p3.set(v3.x+xf, v3.y+yf, v3.z+zf);
		builder.p4.set(v4.x+xf, v4.y+yf, v4.z+zf);
		
		setAmb(lightHigh);
		switch (face) {
		case UP:
			if (!block.isSrclight()) setAmb(lightHigh);
			y1 = isInside ? y : y+1;
			center.set(x, y1, z);
			v1.calcLight(block, center, side1.set(x+1, y1, z), side2.set(x, y1, z-1), corner.set(x+1, y1, z-1));
			v2.calcLight(block, center, side1.set(x-1, y1, z), side2.set(x, y1, z-1), corner.set(x-1, y1, z-1));
			v3.calcLight(block, center, side1.set(x-1, y1, z), side2.set(x, y1, z+1), corner.set(x-1, y1, z+1));
			v4.calcLight(block, center, side1.set(x+1, y1, z), side2.set(x, y1, z+1), corner.set(x+1, y1, z+1));
			break;
		case DOWN:
			if (!block.isSrclight()) setAmb(lightDim);
			y1 = isInside ? y : y-1;
			center.set(x, y1, z);
			v1.calcLight(block, center, side1.set(x-1, y1, z), side2.set(x, y1, z-1), corner.set(x-1, y1, z-1));
			v2.calcLight(block, center, side1.set(x+1, y1, z), side2.set(x, y1, z-1), corner.set(x+1, y1, z-1));
			v3.calcLight(block, center, side1.set(x+1, y1, z), side2.set(x, y1, z+1), corner.set(x+1, y1, z+1));
			v4.calcLight(block, center, side1.set(x-1, y1, z), side2.set(x, y1, z+1), corner.set(x-1, y1, z+1));
			break;
		case SOUTH:
			if (!block.isSrclight()) setAmb(lightMed);
			z1 = isInside ? z : z-1;
			center.set(x, y, z1);
			v1.calcLight(block, center, side1.set(x, y-1, z1), side2.set(x-1, y, z1), corner.set(x-1, y-1, z1));
			v2.calcLight(block, center, side1.set(x, y+1, z1), side2.set(x-1, y, z1), corner.set(x-1, y+1, z1));
			v3.calcLight(block, center, side1.set(x, y+1, z1), side2.set(x+1, y, z1), corner.set(x+1, y+1, z1));
			v4.calcLight(block, center, side1.set(x, y-1, z1), side2.set(x+1, y, z1), corner.set(x+1, y-1, z1));
			break;
		case WEST:
			if (!block.isSrclight()) setAmb(lightLow);
			x1 = isInside ? x : x-1;
			center.set(x1, y, z);
			v1.calcLight(block, center, side1.set(x1, y-1, z), side2.set(x1, y, z+1), corner.set(x1, y-1, z+1));
			v2.calcLight(block, center, side1.set(x1, y+1, z), side2.set(x1, y, z+1), corner.set(x1, y+1, z+1));
			v3.calcLight(block, center, side1.set(x1, y+1, z), side2.set(x1, y, z-1), corner.set(x1, y+1, z-1));
			v4.calcLight(block, center, side1.set(x1, y-1, z), side2.set(x1, y, z-1), corner.set(x1, y-1, z-1));
			break;
		case NORTH:
			if (!block.isSrclight()) setAmb(lightMed);
			z1 = isInside ? z : z+1;
			center.set(x, y, z1);
			v1.calcLight(block, center, side1.set(x, y-1, z1), side2.set(x+1, y, z1), corner.set(x+1, y-1, z1));
			v2.calcLight(block, center, side1.set(x, y+1, z1), side2.set(x+1, y, z1), corner.set(x+1, y+1, z1));
			v3.calcLight(block, center, side1.set(x, y+1, z1), side2.set(x-1, y, z1), corner.set(x-1, y+1, z1));
			v4.calcLight(block, center, side1.set(x, y-1, z1), side2.set(x-1, y, z1), corner.set(x-1, y-1, z1));
			break;
		case EAST:
			if (!block.isSrclight()) setAmb(lightLow);
			x1 = isInside ? x : x+1;
			center.set(x1, y, z);
			v1.calcLight(block, center, side1.set(x1, y-1, z), side2.set(x1, y, z-1), corner.set(x1, y-1, z-1));
			v2.calcLight(block, center, side1.set(x1, y+1, z), side2.set(x1, y, z-1), corner.set(x1, y+1, z-1));
			v3.calcLight(block, center, side1.set(x1, y+1, z), side2.set(x1, y, z+1), corner.set(x1, y+1, z+1));
			v4.calcLight(block, center, side1.set(x1, y-1, z), side2.set(x1, y, z+1), corner.set(x1, y-1, z+1));
			break;
		}
		
		builder.rect(this);
	}
}
