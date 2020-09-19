package com.travall.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder;
import com.badlogic.gdx.utils.Disposable;

public class SSAO implements Disposable {
	
	FrameBuffer fbo;
	
	final ShaderProgram ssaoShaderProgram;
	final FrameBufferBuilder frameBufferBuilder;
	final TextureRegion textureRegion;
	final Camera camera;
	
	public SSAO(Camera camera) {
		this.camera = camera;
		textureRegion = new TextureRegion();
		frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);
		frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
		fbo = frameBufferBuilder.build();

		ssaoShaderProgram = new ShaderProgram(Gdx.files.internal("Shaders/vertex.vs").readString(),Gdx.files.internal("Shaders/fragment.fs").readString());
		Gdx.app.log("ShaderTest", ssaoShaderProgram.getLog());
	}
	
	public void begin() {
		fbo.begin();
	}
	
	public void end() {
		fbo.end();

		Texture diffuseText = fbo.getTextureAttachments().get(1);
		Texture depthText = fbo.getTextureAttachments().get(0);
		
		ssaoShaderProgram.begin();
		depthText.bind(1);
		diffuseText.bind(0);
		ssaoShaderProgram.setUniformi("depthText", 1);
		ssaoShaderProgram.setUniformf("camerarange", camera.near, camera.far);
		ssaoShaderProgram.setUniformf("screensize", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ssaoShaderProgram.end();
		
		textureRegion.setRegion(diffuseText);
		textureRegion.flip(false, true);
	}
	
	public void draw(SpriteBatch spriteBatch) {
		spriteBatch.disableBlending();
		spriteBatch.draw(textureRegion, 0, 0);
		spriteBatch.enableBlending();
	}
	
	public void resize(int width, int height) {
		fbo.dispose();
		GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(width, height);
		frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT32F, GL30.GL_FLOAT);
		frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
		fbo = frameBufferBuilder.build();
	}
	
	@Override
	public void dispose() {
		fbo.dispose();
		ssaoShaderProgram.dispose();
	}
}
