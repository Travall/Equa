package com.travall.game.ui;

import static com.travall.game.Main.main;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.travall.game.ui.utils.UIBase;
import com.travall.game.ui.utils.UImanager;

public class DeletePrompt extends UIBase {

	private final Group group;
	private final Label label;
	private FileHandle folder;

	public DeletePrompt(UImanager manager) {
		group = new Group();
		group.setUserObject(new Vector2(0.5f, 0.5f));

		label = new Label(null, main.skin);
		label.setPosition(0, 50, Align.center);
		group.addActor(label);

		TextButton butt = new TextButton("Cancel", main.skin);
		butt.setSize(100, 25);
		butt.setPosition(-70, -40, Align.center);
		butt.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				manager.setUI(WorldSeletion.class);
			}
		});
		group.addActor(butt);

		butt = new TextButton("Delete", main.skin);
		butt.setSize(100, 25);
		butt.setPosition(70, -40, Align.center);
		butt.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				folder.deleteDirectory();
				manager.setUI(WorldSeletion.class);
			}
		});
		group.addActor(butt);
		
		actors.add(group);
	}

	public void setPromp(FileHandle folder, String name) {
		this.folder = folder;
		label.setText("Are you sure you want to delete the " + name + '?');
		label.pack();
		label.setPosition(0, 50, Align.center);
	}

}
