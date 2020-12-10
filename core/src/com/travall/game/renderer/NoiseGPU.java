package com.travall.game.renderer;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.gl;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class NoiseGPU implements Disposable {
	
	private final Mesh mesh;
	private final FrameBuffer fbo;
	private final ShaderProgram shader;
	
	public final Texture texture;
	
	public int octave = 5;
	public float scale = 5.0f;
	public float gain = 0.4f;
	public float move = 1.0f;
	
	public NoiseGPU() {
		mesh = new Mesh(true, 4, 6, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));
		
		mesh.setIndices(new short[] {0,1,2,2,3,0});
		
		final float[] vertices = new float[8];
		vertices[0] = -1f; // v1.x
		vertices[1] = -1f; // v1.y
		
		vertices[2] = -1f; // v2.x
		vertices[3] = 1f;  // v2.y
		
		vertices[4] =  1f; // v3.x
		vertices[5] =  1f; // v3.y
		
		vertices[6] = 1f; // v4.x
		vertices[7] = -1f;// v4.y
		mesh.setVertices(vertices);
		
		final FrameBufferBuilder builder = new FrameBufferBuilder(Clouds.CLOUD_ROW, Clouds.CLOUD_ROW);
		builder.addColorTextureAttachment(GL30.GL_R32F, GL30.GL_RED, GL30.GL_FLOAT);
		fbo = builder.build();
		texture = fbo.getTextureAttachments().get(0);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		shader = new ShaderProgram(files.internal("Shaders/perlin.vert"), files.internal("Shaders/perlin.frag"));
	}
	
	float time = -1024f;
	
	public void noise() {
		
		time += 0.001f;
		if (time > 1024f) {
			time = -1024f;
		}
		
		fbo.begin();
		
		gl.glDisable(GL20.GL_BLEND);
		shader.bind();
		shader.setUniformi("octave", octave);
		shader.setUniformf("scale", scale);
		shader.setUniformf("gain", gain);
		shader.setUniformf("time", time);
		shader.setUniformf("move", move);
		
		mesh.render(shader, GL30.GL_TRIANGLES);
		
		fbo.end();
		gl.glEnable(GL20.GL_BLEND);
	}

	@Override
	public void dispose() {
		mesh.dispose();
		fbo.dispose();
		shader.dispose();
	}
}
