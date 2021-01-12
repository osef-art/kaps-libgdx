package com.mygdx.stickrush.renderer;

import com.badlogic.gdx.graphics.Color;

public class Rectangle implements Shape {
    private final com.badlogic.gdx.math.Rectangle rect;
    private final Color color;

    public Rectangle(com.badlogic.gdx.math.Rectangle rectangle, Color color) {
        this.rect = rectangle;
        this.color = color;
    }

    @Override
    public void draw(ShapeRendererAdaptor renderer) {
        renderer.drawRect(rect, color);
    }
}
