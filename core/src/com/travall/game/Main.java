package com.travall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.travall.game.blocks.BlocksList;
import com.travall.game.handles.Assets;
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
	public ScreenViewport view;
	public TheMenu menu;
	
	public Texture texture1, texture2; 
	
	private boolean exit;

	@Override
	public void create() {
		preLoad();
		
		Utils.screen.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage = new Stage(view = new ScreenViewport());
		view.setUnitsPerPixel(1/2f);
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, inputs, Inputs.input));
	
		loadAssets();
	}
	
	@Override
	public void render() {
		Gdx.gl.glUseProgram(0); // Fix some performance issues.
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		
		if (exit) {
			super.render();
			return;
		}
		
		if (asset.update()) {
			exit = true;
			getAssets();
			Inputs.clear();
			setScreen(menu = new TheMenu());
		}
		
		// loading screen here.
	}

	private void preLoad() {
		VoxelTerrain.ints(); // Must ints it first.
		texture1 = new Texture("Tiles/ultimate5.png");
		texture2 = new Texture("Tiles/ultimate6.png");
		UltimateTexture.texture = texture1;
		BlocksList.ints();
		Picker.ints();
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glCullFace(GL20.GL_BACK);
	}
	
	@Override
	public void dispose() {
		VoxelTerrain.dispose();
		Picker.dispose();
		asset.dispose();
		skin.dispose();
		
		menu.dispose();
		super.dispose();
		stage.dispose();
		
		texture1.dispose();
		texture2.dispose();
	}
	
	private void loadAssets() {
		asset = new AssetManager();
		
		asset.load("Fonts/Mozart.fnt", BitmapFont.class);
		asset.load("Textures/gui.png", Texture.class);
	}
	
	private void getAssets() {
		Assets.gui = asset.get("Textures/gui.png");
		skin.add("default", asset.get("Fonts/Mozart.fnt"));
		skin.getFont("default").setUseIntegerPositions(true);
		
		loadSkin();
	}
	
	private void loadSkin() {
		LabelStyle lable = new LabelStyle();
		lable.font = skin.getFont("default");
		skin.add("default", lable);
		
		NinePatch ninePatch = new NinePatch(new TextureRegion(Assets.gui, 16, 0, 16, 16), 3, 3, 3, 3);
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(ninePatch);
		ButtonStyle button = new ButtonStyle(ninePatchDrawable.tint(new Color(0.6f, 0.6f, 0.6f, 1)), ninePatchDrawable.tint(new Color(0.4f, 0.7f, 0.7f, 1)), null);
		skin.add("default", button);
		skin.add("default", new TextButtonStyle(button.up, button.down, button.checked, skin.getFont("default")));
	}
}
