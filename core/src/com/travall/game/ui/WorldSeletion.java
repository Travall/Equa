package com.travall.game.ui;

import static com.travall.game.Main.main;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.travall.game.TheMenu;
import com.travall.game.ui.utils.PosOffset;
import com.travall.game.ui.utils.UIBase;
import com.travall.game.ui.utils.UImanager;
import com.travall.game.ui.utils.WorldButton;

public class WorldSeletion extends UIBase {

	private final Group buttons;
	private final TextButton delete;
	private final Array<WorldButton> worldButts;

	public WorldSeletion(TheMenu manu, DeletePrompt delPromp) {
		final UImanager manager = manu.manager;
		buttons = new Group();
		buttons.setUserObject(new PosOffset(0.5f, 0.5f, 0, -10));
		
		Label label = new Label("World Seletion", main.skin);
		label.setUserObject(new Vector2(0.5f, 0.85f));
		label.setAlignment(Align.center);
		actors.add(label);

		final int size = 4;
		worldButts = new Array<>(size);

		TextButtonStyle style = new TextButtonStyle(main.skin.get(TextButtonStyle.class));
		style.checked = style.down;
		delete = new TextButton("Delete", style);
		delete.setSize(80, 25);
		delete.setPosition(-50, (3 - (size + 1)) * 35, Align.center);
		buttons.addActor(delete);

		WorldButton worldButt;
		for (int i = 0; i < size; i++) {
			worldButt = new WorldButton(delete, manu, i+1);
			worldButt.setSize(128, 25);
			worldButt.setPosition(0, (2.2f - i) * 35f, Align.center);
			worldButts.add(worldButt);
			buttons.addActor(worldButt);
		}

		TextButton butt = new TextButton("Back", main.skin);
		butt.setSize(80, 25);
		butt.setPosition(50, (3 - (size + 1)) * 35, Align.center);
		butt.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				manager.setUI(Menu.class);
			}
		});
		buttons.addActor(butt);
		
		actors.add(buttons);
	}

	@Override
	public void show() {
		for (WorldButton butt : worldButts) butt.isWorldExists();
	}

	@Override
	public void hide() {
		delete.setChecked(false);
	}
}
