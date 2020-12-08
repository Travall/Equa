package com.travall.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.travall.game.utils.math.FastNoiseOctaves;

import java.nio.FloatBuffer;
import java.util.Random;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.*;

public class Clouds implements Disposable
{

	private ShaderProgram shader;
	private Mesh sphere;

	private final static int CLOUD_ROW = 200;
	private final static int CLOUD_COUNT = CLOUD_ROW * CLOUD_ROW;
	public int octaves = 5;
	public double persistence = 0.7;
	FastNoiseOctaves noise = new FastNoiseOctaves(octaves,persistence, new Random());

	float offsetX = 0;
	float offsetY = 0;
	float offsetTime = 0;
	
	public static final float DIST = CLOUD_ROW / 2;
	public static final float SIZE = 2f; // 2f
	public static final float SCALE = 5.5f; // 5.5f
	public static final float POWER = 50f; // 40f

	public Clouds() {

		this.shader = new ShaderProgram(files.internal("Shaders/clouds.vert"), files.internal("Shaders/clouds.frag"));
		shader.bind();

		MeshBuilder build = new MeshBuilder();
		build.begin(VertexAttributes.Usage.Position, GL_TRIANGLES);
		SphereShapeBuilder.build(build, 1f, 1f, 1f, 8, 6);
		sphere = build.end();

		sphere.enableInstancedRendering(true, CLOUD_COUNT, 
				new VertexAttribute(Usage.Position, 2, "offset"),new VertexAttribute(Usage.Generic, 1, "scale"));
	}

	FloatBuffer offsets = BufferUtils.newFloatBuffer(CLOUD_COUNT * 3);
	float[] temp = new float[3];
	
	public void render(PerspectiveCamera camera) {

		octaves = (Gdx.input.isKeyJustPressed(Keys.Y) ? octaves - 1 : octaves);
		octaves = (Gdx.input.isKeyJustPressed(Keys.U) ? octaves + 1 : octaves);

		persistence = (Gdx.input.isKeyJustPressed(Keys.H) ? Math.round((persistence - 0.05) * 100.0) / 100.0 : persistence);
		persistence = (Gdx.input.isKeyJustPressed(Keys.J) ? Math.round((persistence + 0.05) * 100.0) / 100.0 : persistence);

		if(Gdx.input.isKeyJustPressed(Keys.Y) || Gdx.input.isKeyJustPressed(Keys.U)
		|| Gdx.input.isKeyJustPressed(Keys.H) || Gdx.input.isKeyJustPressed(Keys.J)) noise = new FastNoiseOctaves(octaves,persistence,1000);

		
		final float x1 = Math.round((camera.position.x+offsetX)/SIZE);
		final float z1 = Math.round((camera.position.z+offsetY)/SIZE);
			
		offsets.clear();
		for (float x = -DIST+x1; x < DIST+x1; x++)
		{
			float xFix = (x-(offsetX/SIZE))*SIZE;
			for (float z = -DIST+z1; z < DIST+z1; z++)
			{				
				float zFix = (z-(offsetY/SIZE))*SIZE;
				float value = noise.getNoise(x/SCALE, z/SCALE, offsetTime) - 0.05f;
				if (value > 0.01f) { // 0.4f
					temp[0] = xFix;
					temp[1] = zFix;
					temp[2] = value * POWER;

					offsets.put(temp);
				}
			}
		}

		offsets.flip();
		sphere.setInstanceData(offsets);
		
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL_BLEND);
		shader.bind();
		shader.setUniformMatrix("projTrans", camera.combined);

		Gdx.gl.glEnable(GL_CULL_FACE);
		Gdx.gl.glDepthMask(false);
		sphere.render(shader, GL_TRIANGLES);
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glDisable(GL_CULL_FACE);
		
		offsetX += 0.03f;
		offsetY += 0.03f;
		offsetTime += 0.01f;
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
