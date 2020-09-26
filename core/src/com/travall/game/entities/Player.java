package com.travall.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.travall.game.generation.MapGenerator;

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
    public int jumpTimer = 0;

    public Player(Vector3 position) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(1,1,1,1f));
        mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        Model model = modelBuilder.createBox(0.75f, 1.9f, 0.75f,mat
                ,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);
        instance.transform.setTranslation(position);

    }

    public void applyForce(Vector3 force) {
        this.acceleration.add(force).scl(0.1f);
    }

    public void update(MapGenerator mapGenerator) {
        this.velocity.add(this.acceleration);

        move(mapGenerator, this.velocity.x, this.velocity.y, this.velocity.z);
        this.acceleration.scl(0);

        this.velocity.x = MathUtils.lerp(this.velocity.x,0,0.2f);
        this.velocity.y = MathUtils.lerp(this.velocity.y,0,0.01f);
        this.velocity.z = MathUtils.lerp(this.velocity.z,0,0.2f);
    }

    public void move(MapGenerator mapGenerator, float x, float y, float z) {

        boolean moveX = true;
        boolean moveY = true;
        boolean moveZ = true;
        onGround = false;
        
        int px, py, pz;
        BoundingBox bintersector;

        final int parts = 10;
        final float temp = parts;

        for(int nx = 1; nx <= parts; nx++) {

        	if (moveX) {
        		instance.transform.translate(x/temp,0,0);
                instance.transform.getTranslation(temp1);

                px = MathUtils.round(temp1.x);
                py = MathUtils.round(temp1.y);
                pz = MathUtils.round(temp1.z);

                bintersector = instance.calculateBoundingBox(boundingBoxTemp).mul(instance.transform);

                if(around(mapGenerator,px,py,pz,bintersector)) {
                	instance.transform.translate(-x/temp,0,0);
                    this.velocity.x = 0;
                	moveX = false;
                }
        	}
            
        	if (moveY) {
        		instance.transform.translate(0, y/temp, 0);
                instance.transform.getTranslation(temp1);

                px = MathUtils.round(temp1.x);
                py = MathUtils.round(temp1.y);
                pz = MathUtils.round(temp1.z);

                bintersector = instance.calculateBoundingBox(boundingBoxTemp).mul(instance.transform);

                if (around(mapGenerator, px, py, pz, bintersector)) {
                	onGround = true;
                    instance.transform.translate(0, -y/temp, 0);
                    this.velocity.y = 0;
                	moveY = false;
                }
        	}
            
        	if (moveZ) {
        		 instance.transform.translate(0, 0, z/temp);
                 instance.transform.getTranslation(temp1);

                 px = MathUtils.round(temp1.x);
                 py = MathUtils.round(temp1.y);
                 pz = MathUtils.round(temp1.z);

                 bintersector = instance.calculateBoundingBox(boundingBoxTemp).mul(instance.transform);

                 if (around(mapGenerator, px, py, pz, bintersector)) {
                	 instance.transform.translate(0, 0, -z/temp);
                     this.velocity.z = 0;
                	 moveZ = false;
                 }
        	}
        	
        	if (!moveX && !moveY && !moveZ) break;
        }
    }

    boolean intersects(MapGenerator mapGenerator, int x, int y, int z, BoundingBox bintersector) {
        temp2.set(temp1.set(x,y,z)).add(1, 1, 1);
        return mapGenerator.blockExists(x,y,z) && boundingBox.set(temp1,temp2).intersects(bintersector);
    }


    boolean around(MapGenerator mapGenerator, int x, int y, int z, BoundingBox bintersector) {
        for(int xx = x - 1; xx <= x + 1; xx++) {
            for(int yy = y - 2; yy <= y + 2; yy++) {
                for(int zz = z - 1; zz <= z + 1; zz++) {
                    if(intersects(mapGenerator,xx,yy,zz,bintersector)) {
                    	return true;
                    }
                }
            }
        }
        return false;
    }
}
