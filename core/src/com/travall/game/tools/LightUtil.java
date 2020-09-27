package com.travall.game.tools;

public final class LightUtil {
	/** Get the bits XXXX0000 */
	public static int getSunLight(int light) {
	    return (light >>> 4) & 0xF;
	}
	
	/** Get the bits 0000XXXX */
	public static int getSrcLight(int light) {
	    return light & 0xF;
	}
}
