package com.travall.game.utils;

public final class LightUtil {
	public static final int   fullLight = 15;
	public static final float lightScl  = fullLight;
	
	/** Get the bits XXXX0000 */
	public static int getSunLight(int light) {
	    return (light >>> 4) & 0xF;
	}
	
	/** Get the bits 0000XXXX */
	public static int getSrcLight(int light) {
	    return light & 0xF;
	}
}
