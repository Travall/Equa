package com.travall.game.renderer;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.gl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;

public class Clouds implements Disposable
{
	
	final static int CLOUD_ROW = 256;
	private final static int CLOUD_COUNT = CLOUD_ROW * CLOUD_ROW;
	private static final float[] ARRAY = new float[CLOUD_COUNT << 1];

	private final ShaderProgram shader;
	private final Mesh sphere;
	private final NoiseGPU gpu;

	public Clouds() {
		shader = new ShaderProgram(files.internal("Shaders/clouds.vert"), files.internal("Shaders/clouds.frag"));
		shader.bind();

		MeshBuilder build = new MeshBuilder();
		build.begin(VertexAttributes.Usage.Position, GL20.GL_TRIANGLES);
		SphereShapeBuilder.build(build, 1f, 1f, 1f, 8, 6);
		sphere = build.end();
		sphere.enableInstancedRendering(true, CLOUD_COUNT, new VertexAttribute(Usage.Position, 2, "offset"));
		
		gpu = new NoiseGPU();
	}
	
	private static final Pool<GridPoint2> POOL = new Pool<GridPoint2>(CLOUD_ROW) {
		protected GridPoint2 newObject() {
			return new GridPoint2();
		}
	};
	
	private static boolean[][] BOOLS = new boolean[CLOUD_ROW][CLOUD_ROW];
	private static void reset() {
		for (int x = 0; x < CLOUD_ROW; x++)
		for (int z = 0; z < CLOUD_ROW; z++) {
			BOOLS[x][z] = false;
		}
	}
	
	private final Queue<GridPoint2> queue = new Queue<>(CLOUD_ROW);
	
	public void render(PerspectiveCamera camera) {
		
		gpu.octave = 4;
		gpu.scale = 3f;
		gpu.gain = 0.5f;
		gpu.move = 0.2f;
		//float offset = 20.0f;
		float shift = -0.05f;
		float size = 30.0f;
		
		final Vector3 pos = camera.position;
		
		long a = System.currentTimeMillis();
		reset();
		int i = (CLOUD_COUNT-1) << 1;
		queue.addLast(POOL.obtain().set(MathUtils.floor(pos.x)>>1, MathUtils.floor(pos.z)>>1));
		while (queue.notEmpty()) {
			final GridPoint2 point = queue.removeFirst();
			
			final int x, z;
			x = point.x;
			z = point.y;
			
			if (x-1 >= 0 && !BOOLS[x-1][z]) {
				queue.addLast(POOL.obtain().set(x-1, z));
				BOOLS[x-1][z] = true;
				ARRAY[i]   = x-1 << 1;
				ARRAY[i+1] = z << 1;
				i -= 2;
			}
			if (x+1 < CLOUD_ROW && !BOOLS[x+1][z]) {
				queue.addLast(POOL.obtain().set(x+1, z));
				BOOLS[x+1][z] = true;
				ARRAY[i]   = x+1 << 1;
				ARRAY[i+1] = z << 1;
				i -= 2;
			}
			if (z-1 >= 0 && !BOOLS[x][z-1]) {
				queue.addLast(POOL.obtain().set(x, z-1));
				BOOLS[x][z-1] = true;
				ARRAY[i]   = x << 1;
				ARRAY[i+1] = z-1 << 1;
				i -= 2;
			}
			if (z+1 < CLOUD_ROW && !BOOLS[x][z+1]) {
				queue.addLast(POOL.obtain().set(x, z+1));
				BOOLS[x][z+1] = true;
				ARRAY[i]   = x << 1;
				ARRAY[i+1] = z+1 << 1;
				i -= 2;
			}
			
			POOL.free(point);
		}
		sphere.setInstanceData(ARRAY);
		System.out.println(System.currentTimeMillis() - a);
		
		gpu.noise();
		gpu.texture.bind();
		
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL20.GL_BLEND);
		shader.bind();
		shader.setUniformMatrix("projTrans", camera.combined);
		shader.setUniformf("shift", shift);
		shader.setUniformf("size", size);
		shader.setUniformf("rows", 512);
		
		shader.setUniformf("cloudPower",  0.8f);
		shader.setUniformf("cloudClamp",  0.5f);
		float yOffset = (200f - pos.y) * 0.02f;
		shader.setUniformf("cloudOffset", -MathUtils.clamp(yOffset, -0.45f, -0.15f));

		Gdx.gl.glCullFace(GL20.GL_FRONT);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glDepthMask(false);
		sphere.render(shader, GL20.GL_TRIANGLES);
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
	}

	@Override
	public void dispose() {
		shader.dispose();
		sphere.dispose();
		gpu.dispose();
	}
}
