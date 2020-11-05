package com.travall.game.ui;

import static com.travall.game.Main.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.travall.game.Main;
import com.travall.game.TheMenu;
import com.travall.game.WorldLoading;
import com.travall.game.ui.utils.PosOffset;
import com.travall.game.ui.utils.UIBase;

public class Menu extends UIBase {
	
	public final Label waterMark;
	public final Group buttons;
	
	public Menu(TheMenu menu) {
		waterMark = new Label("Equa " + Main.VERSION, main.skin);
		waterMark.setAlignment(Align.topLeft);
		waterMark.setUserObject(new PosOffset(0f, 1f, 3, -3));
		actors.add(waterMark);
		
		buttons = new Group();
		buttons.setUserObject(new Vector2(0.5f, 0.4f));
		actors.add(buttons);
		
		TextButton butt = new TextButton("Start", main.skin);
		butt.setSize(128, 28);
		butt.setPosition(0, 24, Align.center);
		butt.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				main.setScreen(new WorldLoading(menu.background));
			}
		});
		buttons.addActor(butt);
		
		butt = new TextButton("Exit", main.skin);
		butt.setSize(128, 28);
		butt.setPosition(0, -24, Align.center);
		butt.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		buttons.addActor(butt);
	}
}
