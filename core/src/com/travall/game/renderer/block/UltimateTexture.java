package com.travall.game.renderer.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public final class UltimateTexture implements Disposable {
	public static final int textureSize = 256;
	public static final int regionSize = 16;

	public final Texture currentTexture;

	public UltimateTexture(Texture texture) {
		this.currentTexture = texture;
	}

	public TextureRegion createRegion(int indexX, int indexY) {
		return new TextureRegion(currentTexture, indexX*regionSize, indexY*regionSize, regionSize, regionSize);
	}

	@Override
	public void dispose() {
		currentTexture.dispose();
	}
}
