package com.travall.game.renderer.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class UltimateTexture {
	public static final int textureSize = 256;
	public static final int regionSize = 16;
	
	public static Texture texture;

	public static TextureRegion createRegion(int indexX, int indexY) {
		return new TextureRegion(texture, indexX*regionSize, indexY*regionSize, regionSize, regionSize);
	}

	public static void dispose() {
		if (texture == null) return;
		texture.dispose();
		texture = null;
	}
}
