package com.travall.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.travall.game.world.World;
import com.travall.game.blocks.Air;
import com.travall.game.blocks.Block;
import com.travall.game.blocks.materials.Material;
import com.travall.game.tools.FirstPersonCameraController;

public class Player
{
    public ModelInstance instance;
    BoundingBox boundingBox = new BoundingBox();
    Vector3 temp1 = new Vector3();
    Vector3 temp2 = new Vector3();
    BoundingBox boundingBoxTemp = new BoundingBox();
    Vector3 velocity = new Vector3();
    Vector3 acceleration = new Vector3();
    
    public boolean onGround;
    public boolean isFlying = false;

    public Player(Vector3 position) {
        ModelBuilder modelBuilder = new ModelBuilder();
        com.badlogic.gdx.graphics.g3d.Material mat = new com.badlogic.gdx.graphics.g3d.Material(ColorAttribute.createDiffuse(1,1,1,1f));
        mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        Model model = modelBuilder.createBox(0.7f, 1.85f, 0.7f,mat
                ,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);
        instance.transform.setTranslation(position);
    }

    public void applyForce(Vector3 force) {
        acceleration.add(force);
    }
    
    public void reset() {
    	velocity.setZero();
    	acceleration.setZero();
    }

    public void setPosition(Vector3 position) {
        instance.transform.setTranslation(position);
    }
    public Vector3 getPosition() {
        return instance.transform.getTranslation(new Vector3());
    }

    public void update(World world, Camera camera, FirstPersonCameraController cameraController) {
        process(camera,cameraController);
        velocity.add(acceleration);
        move(world, this.velocity.x, this.velocity.y, this.velocity.z);
        acceleration.setZero();
        
        velocity.x = MathUtils.lerp(this.velocity.x,0,0.2f);
        velocity.y = isFlying ? MathUtils.lerp(this.velocity.y,0,0.1f) : MathUtils.lerp(this.velocity.y,0,0.01f);
        velocity.z = MathUtils.lerp(this.velocity.z,0,0.2f);
    }

    final Vector3 add = new Vector3(), direction = new Vector3(), noam = new Vector3(), temp = new Vector3();
    public void process(Camera camera, FirstPersonCameraController cameraController) {
        float y = this.isFlying ? 0 : -0.015f;
        float speed = 0.0175f;

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if(this.onGround) {
                y = 0.2f;
            } else if(this.isFlying) {
                y = 0.03f;
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && this.isFlying) {
            y = -0.03f;
        }

        noam.set(camera.direction).nor();
        float angle = MathUtils.atan2(noam.x, noam.z);

//        player.instance.nodes.first().rotation.set(Vector3.Y,angle);
//        player.instance.calculateTransforms();

        direction.set(MathUtils.sin(angle),0,MathUtils.cos(angle));
        add.setZero();

        temp.set(direction);

        if(Gdx.input.isKeyPressed(Input.Keys.W)) add.add(temp.scl(speed * (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? (this.isFlying ? 3f : 1.5f) : 1)));
        if(Gdx.input.isKeyPressed(Input.Keys.S)) add.add(temp.scl(-speed));

        temp.set(direction.rotate(Vector3.Y,-90));

        if(Gdx.input.isKeyPressed(Input.Keys.A)) add.add(temp.scl(-speed * (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? (this.isFlying ? 1.5f : 1f) : 1)));
        if(Gdx.input.isKeyPressed(Input.Keys.D)) add.add(temp.scl(speed * (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? (this.isFlying ? 1.5f : 1f) : 1)));

        if(!add.equals(Vector3.Zero) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.W)) cameraController.targetFOV = 90; // changed from 110 to 90
        else cameraController.targetFOV = 80; // changed from 90 to 80

        add.y = y;
        this.applyForce(add);
    }

    public void move(World world, float x, float y, float z) {

        boolean moveX = true;
        boolean moveY = true;
        boolean moveZ = true;
        onGround = false;
        
        int px, py, pz;
        BoundingBox bintersector;

        final int parts = 10;
        final float temp = parts;
        final Matrix4 transform = instance.transform;

        for(int nx = 1; nx <= parts; nx++) {

        	if (moveX) {
        		instance.transform.translate(x/temp,0,0);
                instance.transform.getTranslation(temp1);

                px = MathUtils.round(temp1.x);
                py = MathUtils.round(temp1.y);
                pz = MathUtils.round(temp1.z);

                bintersector = instance.calculateBoundingBox(boundingBoxTemp).mul(transform);

                if(around(world,px,py,pz,bintersector)) {
                	transform.translate(-x/temp,0,0);
                    velocity.x = 0;
                	moveX = false;
                }
        	}
            
        	if (moveY) {
        		transform.translate(0, y/temp, 0);
                transform.getTranslation(temp1);

                px = MathUtils.round(temp1.x);
                py = MathUtils.round(temp1.y);
                pz = MathUtils.round(temp1.z);

                bintersector = instance.calculateBoundingBox(boundingBoxTemp).mul(transform);

                if (around(world, px, py, pz, bintersector)) {
                	onGround = true;
                    transform.translate(0, -y/temp, 0);
                    velocity.y = 0;
                	moveY = false;
                }
        	}
            
        	if (moveZ) {
        		 transform.translate(0, 0, z/temp);
                 transform.getTranslation(temp1);

                 px = MathUtils.round(temp1.x);
                 py = MathUtils.round(temp1.y);
                 pz = MathUtils.round(temp1.z);

                 bintersector = instance.calculateBoundingBox(boundingBoxTemp).mul(transform);

                 if (around(world, px, py, pz, bintersector)) {
                	 transform.translate(0, 0, -z/temp);
                     velocity.z = 0;
                	 moveZ = false;
                 }
        	}
        	
        	if (!moveX && !moveY && !moveZ) break;
        }
    }

    boolean intersects(World world, int x, int y, int z, BoundingBox bintersector) {
    	Block block = world.getBlock(x, y, z);
    	
    	if (block.getMaterial().hasCollision()) {
    		Array<BoundingBox> boxes = block.getBoundingBoxes();
    		if (boxes.isEmpty()) return false;
    		for (BoundingBox box : boxes) {
				box.getMin(temp1).add(x, y, z);
				box.getMax(temp2).add(x, y, z);
				if (boundingBox.set(temp1, temp2).intersects(bintersector)) return true;
    		}
    	}
        return false;
    }


    
    boolean around(World world, int x, int y, int z, BoundingBox bintersector) {
        for(int xx = x - 1; xx <= x + 1; xx++) {
            for(int yy = y - 2; yy <= y + 2; yy++) {
                for(int zz = z - 1; zz <= z + 1; zz++) {
                    if(intersects(world,xx,yy,zz,bintersector)) {
                    	return true;
                    }
                }
            }
        }
        return false;
    }
}
