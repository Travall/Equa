package com.travall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.travall.game.blocks.BlocksList;
import com.travall.game.handles.Inputs;
import com.travall.game.renderer.Picker;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.utils.Utils;

public class Main extends Base {
	public static final Main main = new Main();
	private Main () {}
	
	public static final String VERSION = "Alpha 2.0.0";
	
	public AssetManager asset;
	public Skin skin = new Skin();
	public BitmapFont font;
	public ScreenViewport view;
	
	private boolean exit;

	@Override
	public void create() {
		preLoad();
		
		Utils.screen.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage = new Stage(view = new ScreenViewport());
		view.setUnitsPerPixel(0.5f);
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, inputs, Inputs.input));
	
		loadAssets();
	}
	
	@Override
	public void render() {
		Gdx.gl.glUseProgram(0); // Fix some performance issues.
		
		if (exit) {
			super.render();
			return;
		}
		
		if (asset.update()) {
			exit = true;
			getAssets();
			Inputs.clear();
			setScreen(new TheGame());
		}
		
		// loading screen here.
	}

	private void preLoad() {
		VoxelTerrain.ints(); // Must ints it first.
		UltimateTexture.texture = new Texture("Tiles/ultimate6.png");
		BlocksList.ints();
		Picker.ints();
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glCullFace(GL20.GL_BACK);
	}
	
	@Override
	public void dispose() {
		VoxelTerrain.dispose();
		Picker.dispose();
		VisUI.dispose();
		asset.dispose();
		skin.dispose();
		super.dispose();
		stage.dispose();
	}
	
	private void loadAssets() {
		asset = new AssetManager();
		
		asset.load("Fonts/Mozart.fnt", BitmapFont.class);
	}
	
	private void getAssets() {
		font = asset.get("Fonts/Mozart.fnt");
		
		skin.add("default", font);
		
		LabelStyle lable = new LabelStyle();
		lable.font = font;
		skin.add("default", lable);
	}
}
