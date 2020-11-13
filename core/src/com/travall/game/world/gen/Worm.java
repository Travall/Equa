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
	
	private final FastNoiseOctaves noisePitch, noiseYaw;
	private final Vector3 velocity = new Vector3().setToRandomDirection();
	public final Vector3 posision;
	
	private final int size;
	private final float length, steps, offset;
	private float move;
	
	public Worm(Random random, float x, float y, float z) {
		this.noisePitch = new FastNoiseOctaves(3, 0.5, random);
		this.noiseYaw = new FastNoiseOctaves(3, 0.5, random);
		this.posision = new Vector3(x, y, z);
		
		this.steps = 2.0f;
		this.length = 130.0f;
		this.size = 5;
		this.offset = (random.nextFloat() * (length * 0.5f)) - length;
	}
	
	public boolean update() {
		QUAT.setEulerAngles(noiseYaw.getNoise((move + offset) / 28f) * MathUtils.radiansToDegrees, noisePitch.getNoise((move + offset) / 28f)  * MathUtils.radiansToDegrees, 0f);
		QUAT.transform(velocity);
		posision.add(velocity.x * steps, velocity.y * steps * 0.75f, velocity.z * steps);
		
		final int x, y, z;
		x = MathUtils.floor(posision.x);
		y = MathUtils.floor(posision.y);
		z = MathUtils.floor(posision.z);
		
		final int haft = size / 2;
		final double haftd = size / 2.0;
		for (int xx = -haft; xx < haft+1; xx++)
		for (int yy = -haft; yy < haft+1; yy++)
		for (int zz = -haft; zz < haft+1; zz++) {
			if (Math.sqrt(xx*xx + yy*yy + zz*zz) < haftd) {
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
