package com.travall.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.travall.game.tiles.Tile;

public class CameraController extends GestureDetector {
    public int rotateButton = Buttons.LEFT;
    public float rotateAngle = 360f;
    public int translateButton = Buttons.RIGHT;
    public float translateUnits = 10f; // FIXME auto calculate this based on the target
    public float scrollFactor = -0.1f;
    public float pinchZoomFactor = 10f;
    public Vector3 target = new Vector3();
    public static boolean active = true;
    public Camera camera;
    protected int button = -1;
    public Tile[][][] tiles;
    int tileHeight = 0;

    private float startX, startY;
    private Vector3 tmpV1 = new Vector3();
    private Vector2 deltas = new Vector2();
    private Vector2 tstart = new Vector2();
    private Vector2 translate = new Vector2();
    private float zoomAmount;
    private float vertical;

    private Vector3 diff = Vector3.Zero;

    protected static class CameraGestureListener extends GestureAdapter {
        public CameraController controller;
        private float previousZoom;

        @Override
        public boolean touchDown (float x, float y, int pointer, int button) {
            previousZoom = 0;
            return false;
        }

        @Override
        public boolean tap (float x, float y, int count, int button) {
            return false;
        }

        @Override
        public boolean longPress (float x, float y) {
            return false;
        }

        @Override
        public boolean fling (float velocityX, float velocityY, int button) {
            return false;
        }

        @Override
        public boolean pan (float x, float y, float deltaX, float deltaY) {
            return false;
        }

        @Override
        public boolean zoom (float initialDistance, float distance) {
            float newZoom = distance - initialDistance;
            float amount = newZoom - previousZoom;
            previousZoom = newZoom;
            float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
            return controller.pinchZoom(amount / ((w > h) ? h : w));
        }

        @Override
        public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }
    };

    protected final CameraGestureListener gestureListener;

    protected CameraController (final CameraGestureListener gestureListener, final Camera camera) {
        super(gestureListener);
        this.gestureListener = gestureListener;
        this.gestureListener.controller = this;
        this.camera = camera;
        this.target = camera.position.cpy();
    }

    public CameraController (final Camera camera) {
        this(new CameraGestureListener(), camera);
    }

    public void update () {
//        tmpV1.set(camera.direction).crs(camera.up).y = 0f;
        float angle1 = deltas.y * rotateAngle;


        float angle2 = deltas.x * -rotateAngle;


//        if(camera.position.dst(target) < 5) {
//            zoomAmount = -1f;
//        }

        camera.rotateAround(target, Vector3.Y, angle2);
//        camera.rotateAround(target, tmpV1.nor(), angle1);
        camera.translate(tmpV1.set(camera.direction).scl(zoomAmount));

        deltas.x = MathUtils.lerp(deltas.x,0,0.1f);
        deltas.y = MathUtils.lerp(deltas.y,0,0.1f);
        zoomAmount = MathUtils.lerp(zoomAmount,0,0.1f);


    }

    public void updateTarget(Vector3 newT) {
        diff = newT.cpy().sub(target);
        target = newT.cpy();
        camera.translate(diff);
    }

    private int touched;
    private boolean multiTouch;

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if(!active) {
            startX = 0;
            startY = 0;
            deltas.set(0,0);
            translate.set(0,0);
            tstart.set(0,0);
            return false;
        }
        touched |= (1 << pointer);
        multiTouch = !MathUtils.isPowerOfTwo(touched);
        if (multiTouch)
            this.button = -1;
        else if (this.button < 0) {
            startX = screenX;
            startY = screenY;
            this.button = button;
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if(!active) {
            startX = 0;
            startY = 0;
            deltas.set(0,0);
            translate.set(0,0);
            tstart.set(0,0);
            return false;
        }
        touched &= -1 ^ (1 << pointer);
        multiTouch = !MathUtils.isPowerOfTwo(touched);
        if (button == this.button) this.button = -1;
        return super.touchUp(screenX, screenY, pointer, button);
    }

    protected boolean process (float deltaX, float deltaY, int button) {
        if(!active) {
            startX = 0;
            startY = 0;
            deltas.set(0,0);
            translate.set(0,0);
            tstart.set(0,0);
            return false;
        }
        if (button == rotateButton) {
            Vector2 current = new Vector2(deltaX / 5,deltaY / 5);
            deltas.add(current);
        }
        camera.update();
        return true;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if(!active) {
            startX = 0;
            startY = 0;
            deltas.set(0,0);
            translate.set(0,0);
            tstart.set(0,0);
            return false;
        }
        boolean result = super.touchDragged(screenX, screenY, pointer);
        if (result || this.button < 0) return result;
        final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
        final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
        startX = screenX;
        startY = screenY;
        return process(deltaX, deltaY, button);
    }

    @Override
    public boolean scrolled (int amount) {
        return zoom(amount * scrollFactor * translateUnits);
    }

    public boolean zoom (float amount) {
        if(!active) {
            startX = 0;
            startY = 0;
            deltas.set(0,0);
            translate.set(0,0);
            tstart.set(0,0);
            return false;
        }
        zoomAmount = amount;
        camera.update();
        return true;
    }


    protected boolean pinchZoom (float amount) {
        return zoom(pinchZoomFactor * amount);
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }
}