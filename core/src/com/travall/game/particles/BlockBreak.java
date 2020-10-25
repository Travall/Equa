package com.travall.game.particles;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.travall.game.world.World;

public class BlockBreak extends Particle {
	
	private short timer;
	
	public BlockBreak ints(Vector3 position, TextureRegion region) {
		this.setPosition(position.add(random(-0.4f, 0.4f), random(-0.4f, 0.4f), random(-0.4f, 0.4f)));
		
		final int u = random(3) * 4;
		final int v = random(3) * 4;
		this.region.setRegion(region, u, v, 4, 4);
		this.velocity.setToRandomDirection().scl(0.05f);
		this.velocity.y += 0.05f;
		
		timer = 300;
		return this;
	}

	@Override
	public void update(World world) {
		velocity.y -= 0.005f;
		move(world);
		
		if (onGround) {
			velocity.x = MathUtils.lerp(this.velocity.x, 0, 0.1f);
			velocity.z = MathUtils.lerp(this.velocity.z, 0, 0.1f);
		} else {
			velocity.x = MathUtils.lerp(this.velocity.x, 0, 0.02f);
			velocity.z = MathUtils.lerp(this.velocity.z, 0, 0.02f);
		}
	}

	@Override
	public boolean isDead() {
		return --timer <= 0;
	}

}
