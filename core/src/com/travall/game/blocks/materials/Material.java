package com.travall.game.blocks.materials;

public class Material {

	/* Static materials */
	public static final Material AIR = new AirMaterial();
	public static final Material BLOCK = new Material();
	public static final Material LEAVES = new LeavesMaterial();
	public static final Material WATER = new WaterMaterial();
	public static final Material SLAB = new SlabMaterial();
	public static final Material CACTUS = new CactusMaterial();
	public static final Material DIAGONAL = new DiagonalMaterial();

	/* Variables */
	private boolean isSolid = isFullCube();

	/* Getters */

	/** Is full cube. The default is true. */
	public boolean isFullCube() {
		return true;
	}
	
	/** Is block transparent, then put block model in transparent mesh. */
	public boolean isTransparent() {
		return false;
	}

	/** Is block solid. The default is <code>isSolid = isFullCube()</code>. If not true, then it's transparent block. 
	 * @return true if this block has all side solid. Else return false for transparently or custom solid sides. */
	public boolean isSolid() {
		return isSolid;
	}
	
	/** Is block has Collision. The default is <code>isFullCube()</code>. */
	public boolean hasCollision() {
		return isFullCube();
	}

	/** Can it blocks sun's ray. The default is <code>canBlockLights()</code>. */
	public boolean canBlockSunRay() {
		return canBlockLights();
	}

	/** Can it blocks flood lights. The default is <code>isSolid()</code>. */
	public boolean canBlockLights() {
		return isSolid();
	}
	
	/* Setters */
	
	private Material setSolid(boolean bool) {
		isSolid = bool;
		return this;
	}

	/* Utilities */

	private static final StringBuilder build = new StringBuilder();

	@Override
	public String toString() {
		build.setLength(0);
		build.append("isFullCube: ").append(isFullCube()).append('\n');
		build.append("isSolid: ").append(isSolid()).append('\n');
		build.append("hasCollision: ").append(hasCollision()).append('\n');
		build.append("canBlockSunRay: ").append(canBlockSunRay()).append('\n');
		build.append("canBlockLights: ").append(canBlockLights()).append('\n');
		return build.toString();
	}
}
