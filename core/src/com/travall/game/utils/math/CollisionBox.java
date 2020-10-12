package com.travall.game.utils.math;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool;

public class CollisionBox {
	
	public static final Pool<CollisionBox> POOL = new Pool<CollisionBox>() {
		protected CollisionBox newObject() {
			return new CollisionBox();
		}
	};

	public float xMin, yMin, zMin;
	public float xMax, yMax, zMax;

	public CollisionBox() {
	}

	public void expand(float x, float y, float z, CollisionBox out) {
		out.set(min(xMin, xMin+x), min(yMin, yMin+y), min(zMin, zMin+z), max(xMax, xMax+x), max(yMax, yMax+y), max(zMax, zMax+z));
	}

	public CollisionBox set(BoundingBox box) {
		return set(box.min.x, box.min.y, box.min.z, box.max.x, box.max.y, box.max.z);
	}

	public CollisionBox set(CollisionBox box) {
		return set(box.xMin, box.yMin, box.zMin, box.xMax, box.yMax, box.zMax);
	}

	public CollisionBox move(float x, float y, float z) {
		return set(xMin+x, yMin+y, zMin+z, xMax+x, yMax+y, zMax+z);
	}

	public CollisionBox set(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.zMin = zMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.zMax = zMax;
		return this;
	}

	public float collideX(CollisionBox box, float x) {
		if (box.yMax <= yMin || box.yMin >= yMax || box.zMax <= zMin || box.zMin >= zMax) {
			return x;
		}
		return box.xMax<=xMin ? min(xMin-box.xMax,x) : box.xMin>=xMax ? max(xMax-box.xMin,x) : x;
	}

	public float collideY(CollisionBox box, float y) {
		if (box.xMax <= xMin || box.xMin >= xMax || box.zMax <= zMin || box.zMin >= zMax) {
			return y;
		}
		return box.yMax<=yMin ? min(yMin-box.yMax,y) : box.yMin>=yMax ? max(yMax-box.yMin,y) : y;
	}

	public float collideZ(CollisionBox box, float z) {
		if (box.xMax <= xMin || box.xMin >= xMax || box.yMax <= yMin || box.yMin >= yMax) {
			return z;
		}
		return box.zMax<=zMin ? min(zMin-box.zMax,z) : box.zMin>=zMax ? max(zMax-box.zMin,z) : z;
	}
	
	public boolean intersects(CollisionBox box) {
		return xMin < box.xMax && xMax > box.xMin && yMin < box.yMax && yMax > box.yMin && zMin < box.zMax && zMax > box.zMin;
	}
	
	private static float max(float a, float b) {
        return a >= b ? a : b;
    }
	
	private static float min(float a, float b) {
        return a <= b ? a : b;
    }
}
