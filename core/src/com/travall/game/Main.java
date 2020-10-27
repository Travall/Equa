package com.travall.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.VisUI;
import com.travall.game.blocks.*;
import com.travall.game.entities.Player;
import com.travall.game.handles.FirstPersonCameraController;
import com.travall.game.handles.Raycast;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.particles.BlockBreak;
import com.travall.game.particles.ParicleSystem;
import com.travall.game.renderer.Picker;
import com.travall.game.renderer.Skybox;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.utils.BlockPos;
import com.travall.game.world.World;

public class Main extends ApplicationAdapter {
	
	PerspectiveCamera camera;
	FirstPersonCameraController cameraController;

	Skybox skybox;
	World world;

	Block blockType;

	Player player;

	SpriteBatch spriteBatch;
	Texture crosshair;
	
	Texture texture1;
	Texture texture2;
	boolean bool;

	float increase = 0;

	BitmapFont font;
	boolean debug = false;

	final String VERSION = "Alpha 2.0.0";

	@Override
	public void create() {
		VoxelTerrain.ints(); // Must ints it first.
		UltimateTexture.texture = new Texture("Tiles/ultimate6.png");
		texture2 = UltimateTexture.texture;
		texture1 = new Texture("Tiles/ultimate5.png");
		BlocksList.ints();
		blockType = BlocksList.STONE;

		skybox = new Skybox();

		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 1000f;
		camera.update();

		cameraController = new FirstPersonCameraController(camera);
		Gdx.input.setInputProcessor(cameraController);

		world = new World();
		Vector3 starting = new Vector3(World.mapSize / 2, World.mapHeight, World.mapSize / 2);
		player = new Player(starting);

		spriteBatch = new SpriteBatch();

		font = new BitmapFont(Gdx.files.internal("Fonts/Mozart.fnt"));
		font.getData().setScale(2);

		Gdx.input.setCursorCatched(true);

		crosshair = new Texture("crosshair.png");

		Picker.ints();
		ParicleSystem.ints(camera);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
	}

	final StringBuilder info = new StringBuilder();

	@Override
	public void render() {
		update();
		cameraController.update(player.isWalking,player.isFlying);
		camera.update(); // Update the camera projection

		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		skybox.render(camera);
		

		if (Gdx.input.isKeyJustPressed(Keys.F1)) bool = !bool;
		UltimateTexture.texture = bool ? texture2 : texture1;
		world.render(camera);
		Picker.render(camera);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		ParicleSystem.render();
		
		spriteBatch.begin();
		spriteBatch.draw(crosshair, (Gdx.graphics.getWidth() / 2) - 8, (Gdx.graphics.getHeight() / 2) - 8);

		info.setLength(0);
		info.append("Equa ").append(VERSION).append('\n');

		if (debug) {
			info.append('\n');
			info.append("X: ").append(player.getPosition().x).append('\n');
			info.append("Y: ").append(player.getPosition().y).append('\n');
			info.append("Z: ").append(player.getPosition().z).append('\n');
			info.append('\n');
			info.append("Vel X: ").append(player.getVelocity().x).append('\n');
			info.append("Vel Y: ").append(player.getVelocity().y).append('\n');
			info.append("Vel Z: ").append(player.getVelocity().z).append('\n');
		}
		font.draw(spriteBatch,info,5,Gdx.graphics.getHeight() - 5);
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

		if(Gdx.input.isKeyJustPressed(Keys.F12)) debug = !debug;

		if (Gdx.input.isKeyJustPressed(Keys.F)) {
			player.isFlying = !player.isFlying;
		}

		if (Gdx.input.isKeyPressed(Keys.Q))
			blockType = BlocksList.TORCH;
		if (Gdx.input.isKeyPressed(Keys.E))
			blockType = BlocksList.DOOR;

		if (Gdx.input.isKeyJustPressed(Keys.P))
			VoxelTerrain.toggleAO();

		camera.position.set(player.getPosition());
		camera.position.add(0f, 1.65f, 0f);

		cameraRaycast();
	}

	@Override
	public void resize(int width, int height) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		//ssao.resize(width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		crosshair.dispose();
		world.dispose();
		skybox.dispose();

		VisUI.dispose();
		VoxelTerrain.dispose();
		Picker.dispose();
		ParicleSystem.dispose();
		texture1.dispose();
		texture2.dispose();
	}

	private final Vector3 tmpVec3 = new Vector3();
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
			Gdx.input.isButtonPressed(Buttons.MIDDLE) ? Buttons.MIDDLE :
			Gdx.input.isButtonPressed(Buttons.RIGHT) ? Buttons.RIGHT : -1;
			
			if (button != -1) {
				
				if(button == Buttons.MIDDLE) {
					blockType = world.getBlock(info.in);
				} else if (!info.blockHit.onClick(player, info, button)) {
					if (button == Buttons.RIGHT) {
						if (!world.isOutBound(info.out.x, info.out.y, info.out.z)) {
							blockType.onPlace(player, info);
						}
					} else if (button == Buttons.LEFT){
						if (blockType.onDestroy(player,info)) {
							for (int i = 0; i < 35; i++)
							ParicleSystem.newParticle(BlockBreak.class)
							.ints(tmpVec3.set(info.in.x+0.5f, info.in.y+0.5f, info.in.z+0.5f), info.blockHit.getBlockModel().getDefaultTexture());
						}
					}
				}
				
			}
		}
	}
}
