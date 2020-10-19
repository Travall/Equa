package com.travall.game.handles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

/** Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * @author badlogic */
public class FirstPersonCameraController extends InputAdapter {
    private final Camera camera;
    private final IntIntMap keys = new IntIntMap();
    private final Vector3 tmp = new Vector3();
    Vector2 mousePos = new Vector2(Gdx.input.getX(),Gdx.input.getY());
    public float targetFOV = 90;

    float camRotateAngle;
    float camTiltAngle;
    Quaternion quat = new Quaternion();

    float mouseSensitivity = 0.5f;

    float increase;

    public FirstPersonCameraController (Camera camera) {
        this.camera = camera;
    }

    public void update(boolean walking, boolean flying) {
        if(walking && !flying) increase += 0.25f;
        else increase = 0;
        camera.rotate(camera.direction, (float) Math.sin(increase) / 48);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        this.mouseMoved(screenX,screenY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        // rotating on the y axis
        float x = mousePos.x -screenX;
        float y = mousePos.y -screenY;

        mousePos.set(screenX,screenY);

//        camera.direction.rotate(camera.up, x * 0.5f);
//        tmp.set(camera.direction).crs(camera.up).nor();
//
//        camera.direction.rotate(tmp, y  * 0.5f);


        camRotateAngle += x * mouseSensitivity;
        camTiltAngle -= y * mouseSensitivity;


        if (camTiltAngle > 89.9) camTiltAngle = 89.9f;
        if (camTiltAngle < -89.9) camTiltAngle = -89.9f;


        //reset quat and camera angles, rotate and apply to camera.
        quat.idt();

        quat.setEulerAngles(camRotateAngle, camTiltAngle, (float) Math.sin(increase) / 48);

        camera.direction.set(0, 0, 1);
        camera.up.set(0,1,0);
        camera.rotate(quat);


        return true;
    }
}