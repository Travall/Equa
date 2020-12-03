package com.travall.game.handles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;

public class FirstPersonCameraController {
    private final Camera camera;
    
    Vector2 mousePos = new Vector2(Gdx.input.getX(),Gdx.input.getY());
    public float targetFOV = 80;

    float camRotateAngle;
    float camTiltAngle;
    Quaternion quat = new Quaternion();

    float mouseSensitivity = 0.3f;

    float increase;

    public FirstPersonCameraController (Camera camera) {
        this.camera = camera;
    }

    public void update(boolean walking, boolean flying) {
        if(walking && !flying) increase += 0.25f;
        else increase = 0;
        camera.rotate(camera.direction, MathUtils.sin(increase) / 48f);
    }

    public void updateDirection() {
        GridPoint2 delta = Inputs.getMouseDelta();

        camRotateAngle += delta.x * mouseSensitivity;
        camTiltAngle -= delta.y * mouseSensitivity;

        if (camTiltAngle > 89.9) camTiltAngle = 89.9f;
        if (camTiltAngle < -89.9) camTiltAngle = -89.9f;

        quat.setEulerAngles(camRotateAngle, camTiltAngle, MathUtils.sin(increase) / 48f);
        camera.direction.set(0, 0, 1);
        camera.up.set(0,1,0);
        camera.rotate(quat);
    }
}