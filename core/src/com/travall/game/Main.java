package com.travall.game;

import static com.travall.game.world.World.world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.VisUI;
import com.travall.game.blocks.*;
import com.travall.game.entities.Player;
import com.travall.game.handles.FirstPersonCameraController;
import com.travall.game.handles.Raycast;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.renderer.Picker;
import com.travall.game.renderer.SSAO;
import com.travall.game.renderer.Skybox;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.utils.BlockPos;
import com.travall.game.world.World;

import java.awt.*;

public class Main extends ApplicationAdapter {
	
	PerspectiveCamera camera;
	FirstPersonCameraController cameraController;
	ModelBatch modelBatch;
	AssetManager assetManager;

	Skybox skybox;
	ModelBatch shadowBatch;
	World world;

	Block blockType;

	Player player;

	Vector3 temp = new Vector3();

	SSAO ssao;
	SpriteBatch spriteBatch;
	Texture crosshair;
	
	Texture texture1;
	Texture texture2;
	boolean bool;

	float increase = 0;

	BitmapFont font;

	@Override
	public void create() {
		VoxelTerrain.ints(); // Must ints it first.
		UltimateTexture.texture = new Texture("Tiles/ultimate6.png");
		texture2 = UltimateTexture.texture;
		texture1 = new Texture("Tiles/ultimate5.png");
		BlocksList.ints();
		blockType = BlocksList.STONE;

		assetManager = new AssetManager();

		DefaultShader.Config defaultConfig = new DefaultShader.Config();
		defaultConfig.numDirectionalLights = 2;
		defaultConfig.fragmentShader = Gdx.files.internal("Shaders/frag.glsl").readString();
		defaultConfig.vertexShader = Gdx.files.internal("Shaders/vert.glsl").readString();

		modelBatch = new ModelBatch(new DefaultShaderProvider(defaultConfig));

		skybox = new Skybox();
		;

		camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 500f;
		camera.update();

		cameraController = new FirstPersonCameraController(camera);
		Gdx.input.setInputProcessor(cameraController);

		shadowBatch = new ModelBatch(new DepthShaderProvider());

//        assetManager.load("Models/steve.g3dj",Model.class);
//        assetManager.finishLoading();
//
//        Model stevey = assetManager.get("Models/steve.g3dj", Model.class);
//        steve = new ModelInstance(stevey);
//        steve.transform.scale(0.3f,0.3f,0.3f);
//        steve.transform.setTranslation(starting.x,starting.y + 2,starting.z);

		world = new World();
		Vector3 starting = new Vector3(World.mapSize / 2, World.mapHeight, World.mapSize / 2);
		player = new Player(starting);

		ssao = new SSAO(camera);
		ssao.setEnable(false); // Enable or disable the SSAO.

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.WHITE);

		Gdx.input.setCursorCatched(true);

		crosshair = new Texture("crosshair.png");

		Picker.ints();
	}

	StringBuilder build = new StringBuilder();

	@Override
	public void render() {
		update();
		camera.update(); // Update the camera projection
		cameraController.update(player.isWalking);

		ssao.begin();
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		skybox.render(camera);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		if (Gdx.input.isKeyJustPressed(Keys.F1)) bool = !bool;
		UltimateTexture.texture = bool ? texture2 : texture1;
		world.render(camera);
		Picker.render(camera);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		
		
		ssao.end();
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		
		ssao.render();

//        spriteBatch.setShader(ssaoShaderProgram);
		spriteBatch.begin();
		spriteBatch.draw(crosshair, (Gdx.graphics.getWidth() / 2) - 8, (Gdx.graphics.getHeight() / 2) - 8);
		font.draw(spriteBatch, "Velocity x: " + player.getVelocity().x, 20, Gdx.graphics.getHeight() - 20);
		spriteBatch.end();
		spriteBatch.setShader(null);

		BlockPos.reset(); // Always reset the pool.
		Gdx.gl.glUseProgram(0); // Fix some performance issues.
	}

	private void update() {

		increase+= 0.15f;

		player.update(world, camera, cameraController);

		// System.out.println(Gdx.graphics.getFramesPerSecond());

		camera.fieldOfView = MathUtils.lerp(camera.fieldOfView, cameraController.targetFOV, 0.2f);

		if (Gdx.input.isKeyJustPressed(Keys.F11)) {
			Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();

			if (Gdx.graphics.isFullscreen()) {
				Gdx.graphics.setWindowedMode(800, 600);
			} else {
				Gdx.graphics.setFullscreenMode(currentMode);
			}
		}

		if (Gdx.input.isKeyJustPressed(Keys.F)) {
			player.isFlying = !player.isFlying;
		}

		if (Gdx.input.isKeyPressed(Keys.Q))
			blockType = BlocksList.TALLGRASS;
		if (Gdx.input.isKeyPressed(Keys.E))
			blockType = BlocksList.GOLD;

		if (Gdx.input.isKeyJustPressed(Keys.P))
			VoxelTerrain.toggleAO();

		camera.position.set(player.getPosition());
		camera.position.add(0f, 1.65f, 0f);

		cameraRaycast();
	}

	@Override
	public void resize(int width, int height) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		ssao.resize(width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void dispose() {
		ssao.dispose();
		spriteBatch.dispose();
		crosshair.dispose();
		world.dispose();
		skybox.dispose();

		modelBatch.dispose();
		assetManager.dispose();
		VisUI.dispose();
		VoxelTerrain.dispose();
		Picker.dispose();
		texture1.dispose();
		texture2.dispose();
		//UltimateTexture.dispose();
	}

	// Fast, accurate, and simple ray-cast.
	private void cameraRaycast() {
		final RayInfo info = Raycast.shot(camera, world);
		if (info == null) {
			Picker.hasHit = false;
			return;
		}
		Picker.hasHit = true;
		Picker.rayInfo = info;
		if (Gdx.input.justTouched()) {
			final int button = 
			Gdx.input.isButtonPressed(Buttons.LEFT) ? Buttons.LEFT : 
			Gdx.input.isButtonPressed(Buttons.RIGHT) ? Buttons.RIGHT : -1;
			
			if (button != -1) {
				if (!blockType.onClick(player, info, button)) {
					if (button == Buttons.RIGHT) {
						if (!world.isOutBound(info.out.x, info.out.y, info.out.z) 
						&& world.isAirBlock(info.out.x, info.out.y, info.out.z)) {
							blockType.onPlace(player, info);
						}
					} else if (button == Buttons.LEFT){
						world.breakBlock(info.in);
					}
				}
			}
		}
	}
}
