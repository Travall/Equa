package com.travall.game.tools;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Utils {

    public static final BlendingAttribute ba = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    public static double normalize(double Input, int max) {
        return ((Input - -1) / (1 - -1) * (max - 0) + 0);
    }


    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
    
    public static int[] locateAttributes(ShaderProgram shader, VertexAttributes attributes) {
		final int s = attributes.size();
		final int[] locations = new int[s];
		for (int i = 0; i < s; i++) {
			final VertexAttribute attribute = attributes.get(i);
			locations[i] = shader.getAttributeLocation(attribute.alias);
		}
		return locations;
	}

    public static Pixmap pixmap = new Pixmap(16,16,Pixmap.Format.RGBA8888);
}
