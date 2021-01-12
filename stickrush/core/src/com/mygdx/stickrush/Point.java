package com.mygdx.stickrush;

public class Point {
    private int x, y;

    public Point(int x, int y) {
        set(x, y);
    }
    public Point(float x, float y) {
        this((int) x, (int) y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
