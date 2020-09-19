package com.travall.game.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;

public class Utils {

    public static final BlendingAttribute ba = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    public static double normalize(double Input, int max) {
        return ((Input - -1) / (1 - -1) * (max - 0) + 0);
    }


    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static Pixmap pixmap = new Pixmap(16,16,Pixmap.Format.RGBA8888);
}
