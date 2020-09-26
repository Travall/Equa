package com.travall.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class SSAO implements Disposable {
	
	FrameBuffer fbo;
	
	final ShaderProgram ssaoShaderProgram;
	final Mesh mesh;
	final Camera camera;
	
	private boolean enable = true;
	
	public SSAO(Camera camera) {
		this.camera = camera;
		createFBO(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		ssaoShaderProgram = new ShaderProgram(Gdx.files.internal("Shaders/vertex.vs").readString(),Gdx.files.internal("Shaders/fragment.fs").readString());
		Gdx.app.log("ShaderTest", ssaoShaderProgram.getLog());
		
		ssaoShaderProgram.bind();
		ssaoShaderProgram.setUniformi("depthText", 1);
		ssaoShaderProgram.setUniformi("u_texture", 2);
		Gdx.gl.glUseProgram(0);
		
		mesh = new Mesh(true, 4, 6, new VertexAttributes(
									new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
									new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE)));
		final short[] indices = {0,1,2,2,3,0};
		mesh.setIndices(indices);
		
		float[] vertices = new float[16];
		vertices[0] = -1f; // v1.x
		vertices[1] = -1f; // v1.y
		vertices[2] = 0f;  // v1.u
		vertices[3] = 0f;  // v1.v
		
		vertices[4] = -1f; // v2.x
		vertices[5] = 1f;  // v2.y
		vertices[6] = 0f;  // v2.u
		vertices[7] = 1f;  // v2.v
		
		vertices[8] =  1f; // v3.x
		vertices[9] =  1f; // v3.y
		vertices[10] = 1f; // v3.u
		vertices[11] = 1f; // v3.v
		
		vertices[12] = 1f; // v4.x
		vertices[13] = -1f;// v4.y
		vertices[14] = 1f; // v4.u
		vertices[15] = 0f; // v4.v
		mesh.setVertices(vertices);
	}
	
	public void begin() {
		if (enable) fbo.begin();
	}
	
	public void end() {
		if (enable) fbo.end();
	}
	
	public void render() {
		if (!enable) return;
		
		ssaoShaderProgram.begin();
		ssaoShaderProgram.setUniformf("camerarange", camera.near, camera.far);
		ssaoShaderProgram.setUniformf("screensize", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mesh.render(ssaoShaderProgram, GL20.GL_TRIANGLES);
		ssaoShaderProgram.end();
	}
	
	public void setEnable(boolean isEnable) {
		enable = isEnable;
	}

	public void resize(int width, int height) {
		fbo.dispose();
		createFBO(width, height);
	}
	
	private void createFBO(int width, int height) {
		GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(width, height);
		frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT32F, GL30.GL_FLOAT);
		frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
		fbo = frameBufferBuilder.build();
		fbo.getTextureAttachments().get(0).bind(1);
		fbo.getTextureAttachments().get(1).bind(2);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
	}
	
	@Override
	public void dispose() {
		fbo.dispose();
		ssaoShaderProgram.dispose();
		mesh.dispose();
	}
}
