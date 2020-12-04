package com.travall.game.ui;

import static com.travall.game.options.Options.*;
import static com.travall.game.Main.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.travall.game.options.RenderDistance;
import com.travall.game.ui.utils.UIBase;
import com.travall.game.ui.utils.UImanager;

public class OptionUI extends UIBase {
	
	// Static access.
	
	public OptionUI(final UImanager manager) {
		final Group group = new Group();
		group.setUserObject(new Vector2(0.5f, 0.5f));
		actors.add(group);
		
		Label label = new Label("Option", main.skin);
		label.setUserObject(new Vector2(0.5f, 0.85f));
		label.setAlignment(Align.center);
		actors.add(label);
		
		final float offset = -25;
		
		TextButton butt = new TextButton("Back", main.skin);
		butt.setSize(128, 29); // 80, 25
		butt.setPosition(0, -60+offset, Align.center);
		butt.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				save(Gdx.app.getPreferences(FILE));
				manager.setUI(Menu.class);
			}
		});
		group.addActor(butt);
		
		final float space = 90;
		final float width = 170;
		
		butt = new TextButton("Graphic: " + (GRAPHIC ? "Fancy" : "Fast"), main.skin);
		butt.setSize(width, 25); // 80, 25
		butt.setPosition(-space, 75+offset, Align.center);
		butt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GRAPHIC = !GRAPHIC;
				((TextButton)actor).setText("Graphic: " + (GRAPHIC ? "Fancy" : "Fast"));
			}
		});
		group.addActor(butt);
		
		butt = new TextButton("Render Distance: " + DISTANCE.toName(), main.skin);
		butt.setSize(width, 25); // 80, 25
		butt.setPosition(space, 75+offset, Align.center);
		butt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				DISTANCE = DISTANCE.toggle();
				((TextButton)actor).setText("Render Distance: " + DISTANCE.toName());
			}
		});
		group.addActor(butt);
		
		butt = new TextButton("Smooth Lighting: On", main.skin);
		butt.setSize(width, 25); // 80, 25
		butt.setPosition(-space, 37.5f+offset, Align.center);
		group.addActor(butt);
		
		butt = new TextButton("Performances...", main.skin);
		butt.setSize(width, 25); // 80, 25
		butt.setPosition(space, 37.5f+offset, Align.center);
		group.addActor(butt);
		
		butt = new TextButton("Particles: All", main.skin);
		butt.setSize(width, 25); // 80, 25
		butt.setPosition(-space, 0+offset, Align.center);
		group.addActor(butt);
		
		butt = new TextButton("Others...", main.skin);
		butt.setSize(width, 25); // 80, 25
		butt.setPosition(space, 0+offset, Align.center);
		group.addActor(butt);
	}
}
