package com.travall.game.handles;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;

public class FirstPersonCameraController {
    public final PerspectiveCamera camera;
    
    public float targetFOV = 80;

    public float camRotateAngle, camTiltAngle;
    private final Quaternion quat = new Quaternion();

    public float mouseSensitivity = 0.3f;

    float increase;

    public FirstPersonCameraController (PerspectiveCamera camera) {
        this.camera = camera;
    }

    public void update(boolean walking, boolean flying) {
        if(walking && !flying) increase += 0.25f;
        else increase = 0;
        camera.rotate(camera.direction, MathUtils.sin(increase) / 48f);
        camera.fieldOfView = MathUtils.lerp(camera.fieldOfView, targetFOV, 0.2f);
    }

    public void updateDirection() {
        GridPoint2 delta = Inputs.getMouseDelta();

        camRotateAngle += delta.x * mouseSensitivity;
        camTiltAngle -= delta.y * mouseSensitivity;

        camTiltAngle = MathUtils.clamp(camTiltAngle, -90f, 90f);

        quat.setEulerAngles(camRotateAngle, camTiltAngle, MathUtils.sin(increase) / 48f);
        camera.direction.set(0, 0, 1);
        camera.up.set(0,1,0);
        camera.rotate(quat);
    }
}