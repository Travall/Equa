package com.travall.game.options;

import com.badlogic.gdx.Preferences;

public final class Options {
	private Options() {}
	
	public static final String FILE = "options.txt";
	public static boolean GRAPHIC = true;
	public static RenderDistance DISTANCE = RenderDistance.NORMAL;

	public static void load(Preferences prefs) {
		GRAPHIC = prefs.getBoolean("Graphic", GRAPHIC);
		DISTANCE = RenderDistance.fromString(prefs.getString("RenderDistance", DISTANCE.toString()));
	}
	
	public static void save(Preferences prefs) {
		prefs.putBoolean("Graphic", GRAPHIC);
		prefs.putString("RenderDistance", DISTANCE.toString());
		prefs.flush();
	}
}
