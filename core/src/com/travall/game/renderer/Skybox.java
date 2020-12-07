package com.travall.game.renderer;

import static com.badlogic.gdx.Gdx.files;
import static com.travall.game.renderer.block.UltimateTexture.createRegion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.travall.game.glutils.VBO;
import com.travall.game.glutils.VBObase;
import com.travall.game.glutils.VertContext;
import com.travall.game.glutils.shaders.ShaderHandle;
import com.travall.game.glutils.shaders.ShaderProgram;
import com.travall.game.renderer.block.UltimateTexture;
import com.travall.game.renderer.vertices.VoxelTerrain;
import com.travall.game.world.World;

public class Skybox implements Disposable
{
	public static final Color SKY = new Color(77/255f,145/255f,255/255f,1f);
	public static final Color FOG = new Color(220/255f,220/255f,220/255f,1f).lerp(SKY, 0.5f);
	
	private static final Color DARK = new Color(SKY).lerp(Color.BLACK, 0.98f);
	private static final Color TMP = new Color();
	
	public float cycle = 0;
		
	private final ShaderProgram skyShader, soonShader;
	private final World world;
	private final Mesh box;
	private final SoonMesh soon;
	private final PerspectiveCamera skyCam = new PerspectiveCamera();
	
	public Skybox(World world) {
		this.world = world;
		
		skyShader = new ShaderProgram(files.internal("Shaders/skybox.vert"), files.internal("Shaders/skybox.frag"));
		soonShader = new ShaderProgram(files.internal("Shaders/soon.vert"), files.internal("Shaders/soon.frag"));
		soon = new SoonMesh(soonShader, createRegion(0, 14), createRegion(0, 15));
		
		MeshBuilder build = new MeshBuilder();
		build.begin(Usage.Position, GL20.GL_TRIANGLES);
		SphereShapeBuilder.build(build, 3f, 3f, 3f, 24, 16);
		box = build.end();
		
		skyCam.near = 0.1f;
		skyCam.far = 10f;
	}
	
	public void render(PerspectiveCamera camera) {
		Gdx.gl.glDepthMask(false);
		//if (Gdx.input.isKeyJustPressed(Keys.R)) reload();
		
		final float level = MathUtils.clamp((MathUtils.sin(world.cycle)+0.2f)*2f, 0f, 1f);
		intsSkyCam(camera);
		skyShader.bind();
		skyShader.setUniformf("u_sky", TMP.set(DARK).lerp(SKY, level));
		skyShader.setUniformf("u_fog", TMP.set(DARK).lerp(FOG, level));
		skyShader.setUniformMatrix("projTrans", skyCam.combined);
		//shader.setUniformf("u_height", pos.y);
		box.render(skyShader, GL20.GL_TRIANGLES);
		Gdx.gl.glUseProgram(0);
		
		// sun and moon
		soon.render(skyCam, world.cycle);
		
		
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
	
	
	@Override
	public void dispose() {
		box.dispose();
		skyShader.dispose();
		soonShader.dispose();
	}
	
	private static class SoonMesh extends VBObase {
		
		private static final Matrix4 MATRIX = new Matrix4();
		
		private static final VertexAttributes ATTRIBUTES = new VertexAttributes(
				new VertexAttribute(Usage.Position, 3, "position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "texCoord")
		);
		
		private final ShaderProgram shader;
		
		public SoonMesh(final ShaderProgram shader, TextureRegion sun, TextureRegion moon) {
			this.glDraw = GL20.GL_STATIC_DRAW;
			this.buffer = VoxelTerrain.BUFFER;
			this.shader = shader;
			
			VertContext context = new VertContext() {
				public ShaderHandle getShader() {
					return shader;
				}
				public VertexAttributes getAttrs() {
					return ATTRIBUTES;
				}
			};
			
			upload(context, true);
			
			FloatArray array = new FloatArray();
			
			final float a = 0.4f;
			
			array.add(5, -a, a);
			array.add(sun.getU2(), sun.getV2());
			array.add(5, a, a);
			array.add(sun.getU2(), sun.getV());
			array.add(5, a, -a);
			array.add(sun.getU(),  sun.getV());
			array.add(5, -a, -a);
			array.add(sun.getU(),  sun.getV2());
			
			array.add(-5, -a, -a);
			array.add(moon.getU2(), moon.getV2());
			array.add(-5, a, -a);
			array.add(moon.getU2(), moon.getV());
			array.add(-5, a, a);
			array.add(moon.getU(),  moon.getV());
			array.add(-5, -a, a);
			array.add(moon.getU(),  moon.getV2());
			
			setVertices(array);
		}
		
		public void render(PerspectiveCamera camera, float cycle) {
			UltimateTexture.texture.bind();
			shader.bind();
			shader.setUniformMatrix("projTrans", camera.combined.rotateRad(Vector3.Z, cycle));
			
			bind();
			Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, 12, GL20.GL_UNSIGNED_SHORT, 0);
			unbind(true);
		}
		
		public void setVertices(FloatArray array) {
			BufferUtils.copy(array.items, buffer, array.size, 0);
			updateVertex();
		}
	}
}
