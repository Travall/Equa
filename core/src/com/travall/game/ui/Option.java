package com.travall.game.ui;

import static com.travall.game.Main.main;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.travall.game.ui.utils.UIBase;
import com.travall.game.ui.utils.UImanager;

public class Option extends UIBase {
	
	// Static access.
	
	public Option(final UImanager manager) {
		final Group group = new Group();
		group.setUserObject(new Vector2(0.5f, 0.5f));
		actors.add(group);
		
		TextButton butt = new TextButton("Back", main.skin);
		butt.setSize(300, 200); // 80, 25
		butt.setPosition(0, 0, Align.center);
		butt.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				manager.setUI(Menu.class);
			}
		});
		group.addActor(butt);
	}
}
