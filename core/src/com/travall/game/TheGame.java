package com.travall.game;

import static com.travall.game.Main.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.BlocksList;
import com.travall.game.entities.Player;
import com.travall.game.handles.FirstPersonCameraController;
import com.travall.game.handles.Inputs;
import com.travall.game.handles.Raycast;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.particles.BlockBreak;
import com.travall.game.particles.ParicleSystem;
import com.travall.game.renderer.Picker;
import com.travall.game.renderer.Skybox;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.ui.utils.PosOffset;
import com.travall.game.utils.BlockPos;
import com.travall.game.world.World;

public class TheGame extends ScreenAdapter {
	
	PerspectiveCamera camera;
	FirstPersonCameraController cameraController;

	Skybox skybox;
	World world;

	Block blockType;

	Player player;

	Texture crosshairTex;
	
	Texture texture1;
	Texture texture2;
	boolean bool;

	float increase = 0;

	boolean debug = false;

	final StringBuilder info = new StringBuilder();
	final Label waterMark;
	final Image crosshair;
	
	public TheGame(World world) {
		blockType = BlocksList.STONE;

		skybox = new Skybox();

		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 1000f;

		cameraController = new FirstPersonCameraController(camera);

		this.world = world;
		Vector3 starting = new Vector3(World.mapSize / 2, World.mapHeight, World.mapSize / 2);
		player = new Player(starting);

		ParicleSystem.ints(camera);
		
		waterMark = new Label("Equa " + Main.VERSION, main.skin);
		waterMark.setAlignment(Align.topLeft);
		waterMark.setUserObject(new PosOffset(0f, 1f, 3, -3));
		
		crosshairTex = new Texture("crosshair.png");
		crosshair = new Image(crosshairTex);
		crosshair.setUserObject(new Vector2(0.5f, 0.5f));
		crosshair.setSize(8, 8);
		
		world.intsMeshes();
	}
	
	@Override
	public void show() {
		main.stage.addActor(waterMark);
		main.stage.addActor(crosshair);
		Gdx.input.setCursorCatched(true);
	}

	@Override
	public void render(final float delta) {
		update();
		cameraController.update(player.isWalking,player.isFlying);
		camera.update(); // Update the camera projection

		skybox.render(camera);
		//if (Gdx.input.isKeyJustPressed(Keys.F1)) bool = !bool;
		//UltimateTexture.texture = bool ? texture2 : texture1;
		
		if (Inputs.isKeyJustPressed(Keys.F1)) bool = !bool;
		UltimateTexture.texture = bool ? main.texture2 : main.texture1;
		world.render(camera);
		Picker.render(camera);
		ParicleSystem.render();
		
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

		main.stage.draw();
		BlockPos.reset(); // Always reset the pool.
		
		if (Inputs.isKeyJustPressed(Keys.ESCAPE)) {
			main.setScreen(new WorldScreen(this));
		}
	}

	private void update() {
		cameraController.updateDirection();

		increase+= 0.15f;

		player.update(world, camera, cameraController);

		// System.out.println(Gdx.graphics.getFramesPerSecond());

		camera.fieldOfView = MathUtils.lerp(camera.fieldOfView, cameraController.targetFOV, 0.2f);

		if (Inputs.isKeyJustPressed(Keys.F11)) {
			Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();

			if (Gdx.graphics.isFullscreen()) {
				Gdx.graphics.setWindowedMode(800, 600);
			} else {
				Gdx.graphics.setFullscreenMode(currentMode);
			}
		}

		if(Inputs.isKeyJustPressed(Keys.F12)) debug = !debug;

		if (Inputs.isKeyJustPressed(Keys.F)) {
			player.isFlying = !player.isFlying;
		}

		if (Inputs.isKeyPressed(Keys.Q))
			blockType = BlocksList.TORCH;
		if (Inputs.isKeyPressed(Keys.E))
			blockType = BlocksList.DOOR;

		if (Inputs.isKeyJustPressed(Keys.P))
			VoxelTerrain.toggleAO();

		camera.position.set(player.getPosition());
		camera.position.add(0f, 1.65f, 0f);

		cameraRaycast();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}
	
	@Override
	public void hide() {
		Gdx.input.setCursorCatched(false);
	}

	private boolean hasDispose;
	
	@Override
	public void dispose() {
		if (hasDispose) return;
		hasDispose = true;
		
		crosshairTex.dispose();
		world.dispose();
		skybox.dispose();

		ParicleSystem.dispose();
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
		if (Inputs.justTouched()) {
			final int button =
			Inputs.isButtonPressed(Buttons.LEFT) ? Buttons.LEFT :
			Inputs.isButtonPressed(Buttons.MIDDLE) ? Buttons.MIDDLE :
			Inputs.isButtonPressed(Buttons.RIGHT) ? Buttons.RIGHT : -1;
			
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
