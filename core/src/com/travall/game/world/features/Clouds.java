package com.travall.game.world.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.utils.math.OpenSimplexOctaves;

import java.nio.FloatBuffer;
import java.util.Random;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.*;

public class Clouds implements Disposable
{

	private ShaderProgram shader;
	private Mesh sphere;
	private final PerspectiveCamera cloudCamera = new PerspectiveCamera();
	private Vector3 position = new Vector3(0,0,0);

	private final static int CLOUD_ROW = 100;
	private final static int CLOUD_COUNT = CLOUD_ROW * CLOUD_ROW;
	OpenSimplexOctaves noise = new OpenSimplexOctaves(5,0.45,new Random());

	float offsetX = 0;
	float offsetY = 0;
	float offsetTime = 0;

	public Clouds() {

		this.shader = new ShaderProgram(files.internal("Shaders/clouds.vert"), files.internal("Shaders/clouds.frag"));
		shader.bind();
		cloudCamera.near = 0.1f;
		cloudCamera.far = 1000;

		MeshBuilder build = new MeshBuilder();
		build.begin(VertexAttributes.Usage.Position, GL20.GL_TRIANGLES);
		SphereShapeBuilder.build(build, 3f, 3f, 3f, 32, 32);
		sphere = build.end();

		sphere.enableInstancedRendering(true, CLOUD_COUNT, new VertexAttribute(VertexAttributes.Usage.Position, 3, "i_offset"),new VertexAttribute(VertexAttributes.Usage.Generic, 1, "i_scale"));
//		mesh.disableInstancedRendering();
	}

	public void render(PerspectiveCamera camera) {

		FloatBuffer offsets = BufferUtils.newFloatBuffer(CLOUD_COUNT * 4);
		for (int x = 1; x <= CLOUD_ROW; x++) {
			for (int y = 1; y <= CLOUD_ROW; y++) {
				float height = (float)noise.getNoise(x + offsetX,y + offsetY,offsetTime) * 2;
				if(height < 0.01) height = 0;
				offsets.put(new float[] {
						x,300,y, height});
			}
		}

		offsets.position(0);
		sphere.setInstanceData(offsets);
//
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL30.GL_BLEND);
		intsCloudCam(camera);
		shader.bind();
		shader.setUniformMatrix("u_projTrans", camera.combined);

		sphere.render(shader, GL20.GL_TRIANGLES);

		offsetX+= 0.05;
		offsetY+= 0.05;
		offsetTime+= 0.05;
	}

	private void intsCloudCam(PerspectiveCamera camera) {
		cloudCamera.direction.set(camera.direction);
		cloudCamera.up.set(camera.up);
		cloudCamera.fieldOfView = camera.fieldOfView;
		cloudCamera.viewportWidth = camera.viewportWidth;
		cloudCamera.viewportHeight = camera.viewportHeight;
		cloudCamera.update(false);
	}

	@SuppressWarnings("unused")
	private void reload() {
		shader.dispose();
		shader = new ShaderProgram(files.internal("Shaders/clouds.vert"), files.internal("Shaders/clouds.frag"));
		shader.bind();
		Gdx.gl.glUseProgram(0);
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
