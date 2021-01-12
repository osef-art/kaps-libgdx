package com.mygdx.stickrush;


import com.mygdx.stickrush.Point;
import com.mygdx.stickrush.ShapeDrawable;

public class Cursor extends ShapeDrawable {
    private final static float size = 10;
    private final Point position;
    private boolean active;

    public Cursor() {
        position = new Point(0, 0);
    }

    public void enable() {
        active = true;
    }

    public void disable() {
        active = false;
    }

    public void update(int x, int y) {
        position.set(x, y);
    }

    public int getX() {
        return position.getX();
    }
    public int getY() {
        return position.getY();
    }

    @Override
    public void render() {
        if (active) sra.drawCircle(position.getX(), position.getY(), size);
    }
}
