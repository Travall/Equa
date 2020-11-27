package com.travall.game.world.gen;

import static com.travall.game.world.World.world;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.BlocksList;
import com.travall.game.utils.math.FastNoiseOctaves;

public class Worm {
	private static final Quaternion QUAT = new Quaternion();
	private static final FastNoiseOctaves ROUGH = new FastNoiseOctaves(3, 0.5, MathUtils.random);
	
	private final FastNoiseOctaves noisePitch, noiseYaw;
	private final Vector3 velocity = new Vector3().setToRandomDirection();
	public final Vector3 posision;
	
	private final int size;
	private final float length, steps, offset;
	private float move;
	
	public Worm(Random random, float x, float y, float z) {
		this.noisePitch = new FastNoiseOctaves(3, 0.2, random);
		this.noiseYaw = new FastNoiseOctaves(3, 0.2, random);
		this.posision = new Vector3(x, y, z);
		
		this.steps = 2.0f;
		this.length = 128.0f;
		this.size = 5;
		this.offset = (random.nextFloat() * (length * 0.5f)) - length;
	}
	
	public boolean update() {
		QUAT.setEulerAngles(noiseYaw.getNoise((move + offset) / 32f) * 180f, noisePitch.getNoise((move + offset) / 32f) * 180f, 0f);
		QUAT.transform(velocity);
		posision.add(velocity.x * steps, velocity.y * steps * 0.7f, velocity.z * steps);
		
		final int x, y, z;
		x = MathUtils.floor(posision.x);
		y = MathUtils.floor(posision.y);
		z = MathUtils.floor(posision.z);
		
		final int haft = (size / 2) + 2;
		final float haftd = size / 2.0f;
		for (int xx = -haft; xx < haft+1; xx++)
		for (int yy = -haft; yy < haft+1; yy++)
		for (int zz = -haft; zz < haft+1; zz++) {
			final double xd, yd, zd;
			xd = xx * xx;
			yd = (yy * yy) * 1.2;
			zd = zz * zz;
			if ((float)Math.sqrt(xd + yd + zd) < haftd+ROUGH.getNoise(xx+x, y+(yy/1.2f), zz+z)*1.1f) {
				world.setBlock(xx+x, yy+y, zz+z, BlocksList.AIR);
			}
		}
		
		return (move += steps) < length;
	}
	
	public static void updateAll(Array<Worm> worms) {
		for (int i = 0, s = worms.size; i < s; i++) {
			final Worm worm = worms.get(i);
			while (worm.update());
		}
	}
}
