package com.travall.game.renderer.block;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BlockTextures {
	
	public final TextureRegion top, bottom,	north, east, south, west;
	
	public BlockTextures(TextureRegion all) {
		top = bottom = north = east = south = west = all;
	}
	
	public BlockTextures(TextureRegion topAndBottom, TextureRegion side) {
		top = bottom = topAndBottom;
		north = east = south = west = side;
	}
	
	public BlockTextures(TextureRegion top, TextureRegion side, TextureRegion bottom) {
		this.top = top;
		this.bottom = bottom;
		north = east = south = west = side;
	}
	
	public BlockTextures(TextureRegion top, TextureRegion side1, TextureRegion side2, TextureRegion bottom) {
		this.top = top;
		this.bottom = bottom;
		north = south = side1;
		east = west = side2;
	}
	
	public BlockTextures(TextureRegion top, TextureRegion bottom, TextureRegion north, TextureRegion east, TextureRegion south, TextureRegion west) {
		this.top = top;
		this.bottom = bottom;
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
	}
}
