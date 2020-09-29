package com.travall.game.tools;

import static com.badlogic.gdx.Gdx.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class Skybox implements Disposable
{
	public static final Color sky = new Color(77/255f,145/255f,255/255f,1f);
	public static final Color fog = new Color(220/255f,220/255f,220/255f,1f);
		
	private ShaderProgram shader;
	private final Mesh box;
	private final PerspectiveCamera skyCam = new PerspectiveCamera();
	
	public Skybox() {
		this.shader = new ShaderProgram(files.internal("Shaders/skybox.vert"), files.internal("Shaders/skybox.frag"));
		shader.bind();
		shader.setUniformf("u_sky", sky);
		shader.setUniformf("u_fog", fog);
		Gdx.gl.glUseProgram(0);
		MeshBuilder build = new MeshBuilder();
		build.begin(Usage.Position, GL20.GL_TRIANGLES);
		SphereShapeBuilder.build(build, 1f, 1f, 1f, 24, 16);
		box = build.end();
		
		skyCam.near = 0.1f;
		skyCam.far = 2f;
	}
	
	public void render(PerspectiveCamera camera) {
		Gdx.gl.glDepthMask(false);
		//if (Gdx.input.isKeyJustPressed(Keys.R)) reload();
		
		intsSkyCam(camera);
		shader.bind();
		shader.setUniformMatrix("u_projTrans", skyCam.combined);
		//shader.setUniformf("u_height", pos.y);
		box.render(shader, GL20.GL_TRIANGLES);
		Gdx.gl.glUseProgram(0);
		
		Gdx.gl.glDepthMask(true);
	}
	
	private void intsSkyCam(PerspectiveCamera camera) {
		skyCam.direction.set(camera.direction);
		skyCam.up.set(camera.up);
		skyCam.fieldOfView = camera.fieldOfView;
		skyCam.viewportWidth = camera.viewportWidth;
		skyCam.viewportHeight = camera.viewportHeight;
		skyCam.update(false);
	}
	
	@SuppressWarnings("unused")
	private void reload() {
		shader.dispose();
		shader = new ShaderProgram(files.internal("Shaders/skybox.vert"), files.internal("Shaders/skybox.frag"));
		shader.bind();
		shader.setUniformf("u_sky", sky);
		shader.setUniformf("u_fog", fog);
		Gdx.gl.glUseProgram(0);
	}
	
	@Override
	public void dispose() {
		box.dispose();
		shader.dispose();
	}
}
