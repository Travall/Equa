package com.travall.game.particles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ReflectionPool;
import com.travall.game.world.World;

public final class ParicleSystem {
	
	private static final Array<Particle> particles = new Array<Particle>(false, 64, Particle.class);
	private static ParticleBatch batch;
	
	static {
		setPool(BlockBreak.class);
	}
	
	private static final <T extends Particle> void setPool(Class<T> clazz) {
		Pools.set(clazz, new ReflectionPool<T>(clazz, 64, 1000));
	}
	
	public static void ints(Camera cam) {
		batch = new ParticleBatch(cam);
	}

	/** Render and update the particles. */
	public static void render() {
		if (particles.isEmpty()) return;
		final World world = World.world;
		final Particle[] array = particles.items;
		
		batch.begin();
		for (int i = 0; i < particles.size; i++) {
			final Particle particle = array[i];
			particle.update(world);
			if (particle.isDead()) {
				Pools.free(particles.removeIndex(i--));
			} else {
				batch.draw(particle);
			}
		}
		batch.end();
	}
	
	public static <T extends Particle> T newParticle(Class<T> clazz) {
		final T particle = Pools.obtain(clazz);
		particles.add(particle);
		return particle;
	}

	public static void clear() {
		Pools.freeAll(particles);
		particles.size = 0;
	}
	
	public static void dispose() {
		Pools.freeAll(particles);
		particles.clear();
		batch.dispose();
		batch = null;
	}
}
