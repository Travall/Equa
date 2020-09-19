package com.travall.game.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;

public class Skybox {
    public ModelInstance Generate() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder part = modelBuilder.part("triangle", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,new Material(IntAttribute.createCullFace(GL20.GL_BACK)));

        BoxShapeBuilder boxBuilder = new BoxShapeBuilder();

        Color bottom = new Color(220/255f,220/255f,220/255f,1f);
        Color top = new Color(77/255f,145/255f,255/255f,1f);

        MeshPartBuilder.VertexInfo one = new MeshPartBuilder.VertexInfo().setPos(-1,-1,-1).setCol(bottom);
        MeshPartBuilder.VertexInfo two = new MeshPartBuilder.VertexInfo().setPos(1,-1,-1).setCol(bottom);
        MeshPartBuilder.VertexInfo three = new MeshPartBuilder.VertexInfo().setPos(-1,-1,1).setCol(bottom);
        MeshPartBuilder.VertexInfo four = new MeshPartBuilder.VertexInfo().setPos(1,-1,1).setCol(bottom);
        MeshPartBuilder.VertexInfo five = new MeshPartBuilder.VertexInfo().setPos(-1,1,-1).setCol(top);
        MeshPartBuilder.VertexInfo six = new MeshPartBuilder.VertexInfo().setPos(1,1,-1).setCol(top);
        MeshPartBuilder.VertexInfo seven = new MeshPartBuilder.VertexInfo().setPos(-1,1,1).setCol(top);
        MeshPartBuilder.VertexInfo eight = new MeshPartBuilder.VertexInfo().setPos(1,1,1).setCol(top);

        boxBuilder.build(part,one,two,three,four,five,six,seven,eight);

        Model model = modelBuilder.end();
        return new ModelInstance(model);
    }
}
