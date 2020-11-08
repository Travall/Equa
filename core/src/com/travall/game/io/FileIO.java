package com.travall.game.io;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.files.FileHandle;
import com.travall.game.utils.OSUtils;

public class FileIO {
	public static final String DIR = OSUtils.isWindows ? "AppData/Roaming/Equa/" : "Equa/";

	public static FileHandle external(String path) {
		return files.external(DIR + path);
	}
}
