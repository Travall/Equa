package com.travall.game.ui.utils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class TiledDrawableFix extends TiledDrawable {
	
	public TiledDrawableFix(TextureRegion region) {
		super(region);
	}

	@Override
	public void draw (Batch batch, float x, float y, float width, float height) {
		final float oldColor = batch.getPackedColor();
		batch.setColor(batch.getColor().mul(getColor()));
		super.draw(batch, x, y, width, height);
		batch.setPackedColor(oldColor);
	}
}
