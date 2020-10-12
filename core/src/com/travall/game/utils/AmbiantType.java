package com.travall.game.utils;

public enum AmbiantType {
	NONE(1), DARKEN(0), FULLBRIGHT(1);
	
	public final int value;

	private AmbiantType(int value) {
		this.value = value;
	}
}
