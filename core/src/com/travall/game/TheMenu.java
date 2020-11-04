package com.travall.game;

import static com.travall.game.Main.main;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.ui.Menu;
import com.travall.game.ui.utils.TiledDrawableFix;
import com.travall.game.ui.utils.UImanager;

public class TheMenu extends ScreenAdapter {

	public final UImanager manager = new UImanager();
	
	public Image backgound;
	
	private final Stage stage = main.stage;
	
	public TheMenu() {
		manager.put(new Menu());
		
		TiledDrawableFix tile = new TiledDrawableFix(UltimateTexture.createRegion(1, 0));
		tile.getColor().set(0.6f, 0.6f, 0.6f, 1f);
		backgound = new Image(tile);
		backgound.setUserObject(new Vector2(0.5f, 0.5f));
	}
	
	@Override
	public void show() {
		final Viewport view = stage.getViewport();
		backgound.setSize(view.getWorldWidth(), view.getWorldHeight());
		stage.addActor(backgound);
		
		manager.bind(stage);
		manager.setUI(Menu.class);
	}
	
	@Override
	public void render(float delta) {
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		final Viewport view = stage.getViewport();
		backgound.setSize(view.getWorldWidth(), view.getWorldHeight());
	}
}