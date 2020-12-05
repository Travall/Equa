package com.travall.game;

import static com.travall.game.Main.main;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.travall.game.io.WorldIO.Packet;
import com.travall.game.ui.utils.StageUtils;
import com.travall.game.world.handles.WorldIOhandle;

public class WorldScreen extends ScreenAdapter {
	private final WorldIOhandle worldhandle = new WorldIOhandle();
	
	private final Stage stage = main.stage;
	private final Image background = main.menu.background;
	private final Label label;
	
	private final FileHandle folder;
	private final TheGame game;
	
	private final boolean isSaving;
	
	/** Loading world screen */
	public WorldScreen(FileHandle folder) {
		this.game = null;
		this.folder = folder;
		
		label = new Label(null, main.skin);
		label.setUserObject(new Vector2(0.5f, 0.5f));
		label.setAlignment(Align.center);
		isSaving = false;
	}
	
	/** Saving world screen */
	public WorldScreen(TheGame game) {
		this.game = game;
		this.folder = null;
		
		game.save();
		label = new Label(null, main.skin);
		label.setUserObject(new Vector2(0.5f, 0.5f));
		label.setAlignment(Align.center);
		isSaving = true;
	}
	
	@Override
	public void show() {
		final Viewport view = stage.getViewport();
		background.setSize(view.getWorldWidth(), view.getWorldHeight());
		stage.addActor(background);
		stage.addActor(label);
		
		if (isSaving) {
			worldhandle.save(game.world, game.props);
		} else {
			worldhandle.load(folder);
		}
	}
	
	@Override
	public void render(float delta) {
		label.setText(worldhandle.getStatus());
		label.pack();
		StageUtils.resize(stage);
		stage.draw();
		
		if (worldhandle.isDone()) {
			if (isSaving) {
				main.setScreen(main.menu);
			} else {
				Packet packet = null;
				
				try {
					packet = worldhandle.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				main.setScreen(packet == null ? main.menu : new TheGame(packet));
				
				if (packet != null) {
					packet.props.clear();
					
					packet.world = null;
					packet.props = null;
				}
			}
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
	
	private boolean hasDispose;
	
	@Override
	public void dispose() {
		if (hasDispose) return;
		hasDispose = true;
		
		if (game != null) game.dispose();
		worldhandle.dispose();
	}
}
