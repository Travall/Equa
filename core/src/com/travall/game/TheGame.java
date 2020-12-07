package com.travall.game;

import static com.travall.game.Main.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.travall.game.blocks.Block;
import com.travall.game.entities.Player;
import com.travall.game.handles.Inputs;
import com.travall.game.handles.Raycast;
import com.travall.game.handles.Raycast.RayInfo;
import com.travall.game.io.WorldIO.Packet;
import com.travall.game.items.BlockItem;
import com.travall.game.particles.BlockBreak;
import com.travall.game.particles.ParicleSystem;
import com.travall.game.renderer.Picker;
import com.travall.game.renderer.Skybox;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.ui.actors.BlockSeletion;
import com.travall.game.ui.utils.PosOffset;
import com.travall.game.utils.BlockPos;
import com.travall.game.utils.Properties;
import com.travall.game.world.World;

public class TheGame extends ScreenAdapter {
	
	final PerspectiveCamera camera;
	
	final World world;
	final Stage stage = main.stage;

	BlockItem blockType;
	final Player player;
	final Texture crosshairTex;

	Texture texture1;
	Texture texture2;

	boolean bool;
	float increase = 0;
	boolean debug = false;

	final StringBuilder info = new StringBuilder();
	final Label waterMark;
	final Image crosshair;
	final BlockSeletion seletion = new BlockSeletion();

	/** save/load property */
	final Properties props;

	public TheGame(Packet packet) {
		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 1000f;

		this.props = packet.props;
		this.world = packet.world;
		this.player = new Player(camera);
		if (packet.isLoad) {
			load();
		} else {
			ints();
		}

		ParicleSystem.ints(camera);
		waterMark = new Label("Equa " + Main.VERSION, main.skin);
		waterMark.setAlignment(Align.topLeft);
		waterMark.setUserObject(new PosOffset(0f, 1f, 3, -3));

		crosshairTex = new Texture("crosshair.png");
		crosshair = new Image(crosshairTex);
		crosshair.setUserObject(new Vector2(0.5f, 0.5f));
		crosshair.setSize(8, 8);

		seletion.setUserObject(new PosOffset(0.5f, 0.0f, -16, 12));

		world.intsMeshes();
	}

	@Override
	public void show() {
		stage.addActor(waterMark);
		stage.addActor(crosshair);
		stage.addActor(seletion);
		seletion.addInput();
		Gdx.input.setCursorCatched(true);
	}

	@Override
	public void render(final float delta) {
		update();
		camera.update(); // Update the camera projection

		if (Inputs.isKeyJustPressed(Keys.F1))
			bool = !bool;
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

		stage.act(delta);
		stage.draw();
		BlockPos.reset(); // Always reset the pool.

		if (Inputs.isKeyJustPressed(Keys.ESCAPE)) {
			main.setScreen(new WorldScreen(this));
		}
	}

	private void update() {
		increase += 0.15f;

		player.update(world);

		// System.out.println(Gdx.graphics.getFramesPerSecond());

		

		if (Inputs.isKeyJustPressed(Keys.F11)) {
			Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();

			if (Gdx.graphics.isFullscreen()) {
				Gdx.graphics.setWindowedMode(800, 600);
			} else {
				Gdx.graphics.setFullscreenMode(currentMode);
			}
		}

		if (Inputs.isKeyJustPressed(Keys.F12))
			debug = !debug;

		if (Inputs.isKeyJustPressed(Keys.F)) {
			player.isFlying = !player.isFlying;
		}

		if (Inputs.isKeyJustPressed(Keys.P))
			VoxelTerrain.toggleAO();

		camera.position.set(player.getPosition());
		camera.position.add(0f, 1.65f, 0f);

		blockType = seletion.getBlockItem();
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
		if (hasDispose)
			return;
		hasDispose = true;

		crosshairTex.dispose();
		world.dispose();

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
			Inputs.isButtonPressed(Buttons.LEFT) ? Buttons.LEFT
			: Inputs.isButtonPressed(Buttons.MIDDLE) ? Buttons.MIDDLE
			: Inputs.isButtonPressed(Buttons.RIGHT) ? Buttons.RIGHT : -1;

			if (button != -1) {
				if (button == Buttons.MIDDLE) {
					blockType = new BlockItem(world.getBlock(info.in));
				} else if (!info.blockHit.onClick(player, info, button)) {
					if (button == Buttons.RIGHT) {
						if (!world.isOutBound(info.out.x, info.out.y, info.out.z)) {
							blockType.placeBlock(player, info);
						}
					} else if (button == Buttons.LEFT) {
						final BlockPos in = info.in;
						final Block block = info.blockHit;
						final int type = block.getType(in);
						if (block.onDestroy(in)) {
							for (int i = 0; i < 35; i++) {
								ParicleSystem.newParticle(BlockBreak.class).ints(
										tmpVec3.set(in.x + 0.5f, in.y + 0.5f, in.z + 0.5f),
										block.getBlockModel().getDefaultTexture(null, type));
							}
						}
					}
				}

			}
		}
	}

	// Save properties.
	public void save() {
		final Properties props = this.props;
		player.write(props);
		world.write(props);
	}

	// Load properties.
	private void load() {
		final Properties props = this.props;
		player.read(props);
		world.read(props);
	}
	
	// First time loaded the world.
	private void ints() {
		player.setPosition(new Vector3(World.mapSize / 2, World.mapHeight, World.mapSize / 2));
	}
}
