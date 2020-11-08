package com.travall.game.ui.utils;

import static com.travall.game.Main.main;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.travall.game.TheMenu;
import com.travall.game.WorldScreen;
import com.travall.game.io.WorldIO;
import com.travall.game.ui.DeletePrompt;

public class WorldButton extends TextButton {
	
	public final FileHandle folder;

	public WorldButton(final TextButton deleteButt, TheMenu menu, int num) {
		super(null, main.skin);
		this.folder = WorldIO.getFolder("world" + num);
		setName("World " + num);
		
		final UImanager manager = menu.manager;
		addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (deleteButt.isChecked()) {
					if (folder.isDirectory()) {
						deleteButt.setChecked(false);
						manager.setUI(DeletePrompt.class).setPromp(folder, getName());
					}
				} else {
					main.setScreen(new WorldScreen(folder));
				}
			}
		});
	}

	public boolean isWorldExists() {
		final boolean exists = folder.exists();
		setText(exists ? "*"+getName()+"*" : getName());
		//pack();
		return exists;
	}
}
