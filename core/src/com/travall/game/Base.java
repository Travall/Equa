package com.travall.game;

import com.badlogic.gdx.Game;
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
	
	protected Screen newScreen;
	
	public void scale(float scale) {
		final ScreenViewport view = (ScreenViewport)stage.getViewport();
		view.setUnitsPerPixel(scale);
		view.apply(true);
		super.resize(view.getScreenWidth(), view.getScreenHeight());
		StageUtils.resize(stage);
	}
	
	@Override
	public void render() {
		nextScreen();
		super.render();
		Inputs.reset();
	}
	
	@Override
	public void resize(int width, int height) {
		final Viewport view = stage.getViewport();
		view.update(width, height, true);
		Utils.screen.set(width, height);
		super.resize(width, height);
		StageUtils.resize(stage);
		Inputs.clear();
	}
	
	protected void nextScreen() {
		if (newScreen == null) return;
		
		if (screen != null) screen.hide();
		screen = newScreen;
		newScreen = null;
		
		stage.clear(); // Always clear UI when switching screen.
		inputs.clear(); // Always clear the input processors.
		
		screen.show();
		screen.resize(Utils.screen.w, Utils.screen.h);
		StageUtils.resize(stage);
	}
	
	@Override
	public void setScreen(Screen screen) {
		newScreen = screen;
	}
	
	@Override
	public void dispose() {
		if (screen != null) screen.dispose();
		if (newScreen != null) newScreen.dispose();
	}
}
