package com.mygdx.stickrush.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

import static com.mygdx.stickrush.Game.camera;

public class ShapeRendererAdaptor {
    private final ShapeRenderer rd = new ShapeRenderer();
    private final ArrayList<Shape> shapes = new ArrayList<>();

    public void drawCircle(float x, float y, float radius) {
        rd.setProjectionMatrix(camera.combined);
        rd.begin(ShapeType.Line);
        rd.setColor(1, 1, 1, 1);
        rd.circle(x, y, radius);
        rd.end();
    }

    private void drawRect(float x, float y, float w, float h, Color c, ShapeType type) {
        rd.setProjectionMatrix(camera.combined);
        rd.begin(type);
        rd.setColor(c.r, c.g, c.b, c.a);
        rd.rect(x, y, w, h);
        rd.end();
    }

    private void drawRect(com.mygdx.stickrush.renderer.Rectangle rect) {

    }

    public void drawRect(Rectangle r, Color c) {
        drawRect(r.x, r.y, r.width, r.height, c, ShapeType.Filled);
    }

    public void drawRect(float x, float y, float w, float h, Color c) {
        drawRect(x, y, w, h, c, ShapeType.Filled);
    }

    public void drawOutlineRect(Rectangle r, Color c) {
        drawRect(r.x, r.y, r.width, r.height, c, ShapeType.Line);
    }

    public void drawOutlineRect(float x, float y, float w, float h, Color c) {
        drawRect(x, y, w, h, c, ShapeType.Line);
    }

    public void dispose() {
        rd.dispose();
    }

    public void drawAll() {
        for (Shape shape : shapes) shape.draw(this);
    }

    public void add(Shape shape) {
        shapes.add(shape);
    }
}
