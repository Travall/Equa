package com.travall.game.blocks.materials;

public class Material {
	
	/* Static materials */
	public static final Material AIR    = new AirMaterial();
	public static final Material BLOCK  = new Material();
	public static final Material LEAVES = new Material().setTransparent().setTranslucent();
	public static final Material WATER  = new WaterMaterial();
	
	/* Variables */
	private boolean isTransparent;
	private boolean isTranslucent;
	
	/* Getters */
	
	/** Is full cube. The default is true. */
	public boolean isFullCube() {
    	return true;
    }
	
	/** Is block has Collision. The default is <code>isFullCube()</code>. */
	public boolean hasCollision() {
		return isFullCube();
	}
	
	/** Is block transparent. The default is false. */
	public boolean isTransparent() {
		return isTransparent;
	}
	
	/** Is block translucent. The default is false. */
    public boolean isTranslucent() {
    	return isTranslucent;
    }
    
	/** Can it blocks sun's ray. The default is <code>!isTransparent()</code>. */
    public boolean canBlockSunRay() {
    	return !isTransparent();
    }
    
    /** Can it blocks lights. The default is <code>isFullCube()</code>. */
    public boolean canBlockLights() {
    	return isFullCube();
    }
    
    /* Setters */
    
    protected Material setTransparent() {
    	isTransparent = true;
		return this;
	}
    
    protected Material setTranslucent() {
    	isTranslucent = true;
		return this;
	}
    
    /* Utilities */
    
    protected static final StringBuilder build = new StringBuilder();
    
    @Override
    public String toString() {
    	build.setLength(0);
    	build.append("isFullCube: ").append(isFullCube()).append('\n');
    	build.append("hasCollision: ").append(hasCollision()).append('\n');
    	build.append("isTransparent: ").append(isTransparent()).append('\n');
    	build.append("isTranslucent: ").append(isTranslucent()).append('\n');
    	build.append("canBlockSunRay: ").append(canBlockSunRay()).append('\n');
    	build.append("canBlockLights: ").append(canBlockLights()).append('\n');
    	return build.toString();
    }
    
    @Override
    public Material clone() {
    	final Material material = new Material();
    	if (isTransparent) material.setTransparent();
    	return material;
    }
}
