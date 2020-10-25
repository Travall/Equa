package com.travall.game.renderer;

import static com.badlogic.gdx.Gdx.gl;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.handles.Raycast.RayInfo;

public final class Picker {
	
	public static RayInfo rayInfo = new RayInfo();
	public static boolean hasHit;
	
	private static ShapeRenderer shape;
	private static final PerspectiveCamera pickCam = new PerspectiveCamera();
	
	public static void ints() {
		shape = new ShapeRenderer(100);
		shape.setAutoShapeType(true);
		shape.setColor(0f, 0f, 0f, 0.6f);
		pickCam.far = 1020;
		pickCam.near = 0.101f;
	}
	
	public static void render(PerspectiveCamera camera) {
		if (!hasHit) return;
		gl.glLineWidth(2);
		gl.glEnable(GL20.GL_BLEND);
		intsCam(camera);
		shape.setProjectionMatrix(pickCam.combined);
		shape.begin(ShapeType.Line);
		final BoundingBox box = rayInfo.boxHit;
		final float x = rayInfo.in.x, y = rayInfo.in.y, z = rayInfo.in.z+box.getDepth();
		shape.box(box.min.x+x, box.min.y+y, box.min.z+z, box.getWidth(), box.getHeight(), box.getDepth());
		shape.end();
		gl.glDisable(GL20.GL_BLEND);
	}
	
	private static void intsCam(PerspectiveCamera camera) {
		pickCam.position.set(camera.position);
		pickCam.direction.set(camera.direction);
		pickCam.up.set(camera.up);
		pickCam.fieldOfView = camera.fieldOfView;
		pickCam.viewportWidth = camera.viewportWidth;
		pickCam.viewportHeight = camera.viewportHeight;
		pickCam.update(false);
	}

	public static void dispose() {
		shape.dispose();
	}
}
