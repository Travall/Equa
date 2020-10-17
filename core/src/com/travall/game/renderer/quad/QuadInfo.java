package com.travall.game.renderer.quad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.travall.game.renderer.vertices.VertInfo;

public class QuadInfo {
	public final VertInfo
	v1 = new VertInfo(),
	v2 = new VertInfo(),
	v3 = new VertInfo(),
	v4 = new VertInfo();
	
	/** Optional for mesh builder */
	public final TextureRegion region = new TextureRegion();
	
	public void setAmb(float value) {
		v1.ambLit = value;
		v2.ambLit = value;
		v3.ambLit = value;
		v4.ambLit = value;
	}
	
	public void setSrc(float value) {
		v1.srcLit = value;
		v2.srcLit = value;
		v3.srcLit = value;
		v4.srcLit = value;
	}
	
	public void setSun(float value) {
		v1.sunLit = value;
		v2.sunLit = value;
		v3.sunLit = value;
		v4.sunLit = value;
	}
	
	public void mul(float x, float y, float z) {
		this.v1.mul(x,y,z);
		this.v2.mul(x,y,z);
		this.v3.mul(x,y,z);
		this.v4.mul(x,y,z);
	}

	public void add(float x, float y, float z) {
		this.v1.add(x,y,z);
		this.v2.add(x,y,z);
		this.v3.add(x,y,z);
		this.v4.add(x,y,z);
	}
}
