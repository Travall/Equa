package com.travall.game.utils;

public final class OSUtils {
	public static final boolean
	isWindows = System.getProperty("os.name").contains("Windows"),
	isLinux = System.getProperty("os.name").contains("Linux"),
	isMac = System.getProperty("os.name").contains("Mac");
}
