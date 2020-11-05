package com.travall.game;

import static com.travall.game.Main.main;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.travall.game.ui.utils.StageUtils;
import com.travall.game.world.handles.WorldGen;

public class WorldLoading extends ScreenAdapter {
	private final WorldGen worldGen = new WorldGen();
	private final Stage stage = main.stage;
	private final Image background;
	private final Label label;
	
	public WorldLoading(Image background) {
		this.background = background;
		
		label = new Label(null, main.skin);
		label.setUserObject(new Vector2(0.5f, 0.5f));
		label.setAlignment(Align.center);
	}
	
	@Override
	public void show() {
		final Viewport view = stage.getViewport();
		background.setSize(view.getWorldWidth(), view.getWorldHeight());
		stage.addActor(background);
		stage.addActor(label);
		
		worldGen.run();
	}
	
	@Override
	public void render(float delta) {
		label.setText(worldGen.getStatus());
		label.pack();
		
		StageUtils.resize(stage);
		stage.draw();
		
		if (worldGen.isDone()) {
			main.setScreen(new TheGame(worldGen.get()));
		}
	}
	
	@Override
	public void resize(int width, int height) {
		final Viewport view = stage.getViewport();
		background.setSize(view.getWorldWidth(), view.getWorldHeight());
	}
	
	@Override
	public void hide() {
		dispose();
	}
	
	@Override
	public void dispose() {
		worldGen.dispose();
	}
}
