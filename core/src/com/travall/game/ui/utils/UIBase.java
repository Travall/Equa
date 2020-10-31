package com.travall.game.ui.utils;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class UIBase extends InputAdapter implements UI {
	
	/** Use for auto binding and visible. */
	protected final Array<Actor> actors = new Array<>();

	@Override
	public void bind(Stage stage) {
		for (Actor actor : actors) stage.addActor(actor);
	}
	
	@Override
	public void setVisible(boolean visible) {
		for (Actor actor : actors) actor.setVisible(visible);
		if (visible) {
			show();
		} else {
			hide();
		}
	}
	
	public void show() {
		
	}
	
	public void hide() {
		
	}
}
