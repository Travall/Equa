package com.travall.game.options;

public enum RenderDistance {
	SHORT(6), NORMAL(10), FAR(16), EXTREME(24);
	
	public final int chunks;
	
	private RenderDistance(int chunks) {
		this.chunks = chunks;
	}
	
	public String toName() {
		switch (this) {
		case SHORT: return "Short";
		case NORMAL: return "Normal";
		case FAR: return "Far";
		case EXTREME: return "Extreme";
		default: return null;
		}
	}
	
	public RenderDistance toggle() {
		switch (this) {
		case SHORT: return NORMAL;
		case NORMAL: return FAR;
		case FAR: return EXTREME;
		case EXTREME: return SHORT;
		default: return null;
		}
	}

	public static RenderDistance fromString(String string) {
		switch (string) {
		case "SHORT": return SHORT;
		case "NORMAL": return NORMAL;
		case "FAR": return FAR;
		case "EXTREME": return EXTREME;
		default: return null;
		}
	}
}
