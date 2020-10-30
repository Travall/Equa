package com.travall.game;

import static com.badlogic.gdx.math.MathUtils.roundPositive;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.travall.game.handles.Inputs;
import com.travall.game.ui.utils.StageUtils;
import com.travall.game.utils.Utils;

abstract class Base extends Game {
	
	public Stage stage;
	
	public final InputMultiplexer inputs = new InputMultiplexer();
	
	public void scale(float scale) {
		final ScreenViewport view = (ScreenViewport)stage.getViewport();
		view.setUnitsPerPixel(scale);
		view.apply(true);
		Utils.world.set(roundPositive(view.getWorldWidth()), roundPositive(view.getWorldHeight()));
		super.resize(view.getScreenWidth(), view.getScreenHeight());
		StageUtils.resize(stage);
	}
	
	@Override
	public void render() {
		super.render();
		Inputs.reset();
	}
	
	@Override
	public void resize(int width, int height) {
		final Viewport view = stage.getViewport();
		view.update(width, height, true);
		Utils.screen.set(width, height);
		Utils.world.set(roundPositive(view.getWorldWidth()), roundPositive(view.getWorldHeight()));
		super.resize(width, height);
		StageUtils.resize(stage);
		Inputs.clear();
	}
	
	@Override
	public void setScreen(Screen screen) {
		if (this.screen != null) this.screen.hide();
		this.screen = screen;
		
		stage.clear(); // Always clear UI when switching screen.
		inputs.clear(); // Always clear the input processors.
		
		if (screen == null) return;
		screen.show();
		screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		StageUtils.resize(stage);
	}
	
	@Override
	public void dispose() {
		if (screen != null) screen.dispose();
	}
}