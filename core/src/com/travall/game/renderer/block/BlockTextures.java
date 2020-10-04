package com.travall.game.renderer.block;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BlockTextures {
	
	public final TextureRegion top, bottom,	render1, render2, render3, render4;
	
	public BlockTextures(TextureRegion all) {
		top = bottom = render1 = render2 = render3 = render4 = all;
	}
	
	public BlockTextures(TextureRegion topAndBottom, TextureRegion side) {
		top = bottom = topAndBottom;
		render1 = render2 = render3 = render4 = side;
	}
	
	public BlockTextures(TextureRegion top, TextureRegion side, TextureRegion bottom) {
		this.top = top;
		this.bottom = bottom;
		render1 = render2 = render3 = render4 = side;
	}
	
	public BlockTextures(TextureRegion top, TextureRegion side1, TextureRegion side2, TextureRegion bottom) {
		this.top = top;
		this.bottom = bottom;
		render1 = render3 = side1;
		render2 = render4 = side2;
	}
	
	public BlockTextures(TextureRegion top, TextureRegion bottom, TextureRegion render1, TextureRegion render2, TextureRegion render3, TextureRegion render4) {
		this.top = top;
		this.bottom = bottom;
		this.render1 = render1;
		this.render2 = render2;
		this.render3 = render3;
		this.render4 = render4;
	}
}
