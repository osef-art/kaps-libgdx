package com.gdx.kaps.level.grid;

public class Position {
    private int x, y;

    public Position(float x, float y) {
        // IMPL: vector...
        this((int) x, (int) y);
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
        this(0, 0);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void set(Position pos) {
        set(pos.x, pos.y);
    }

    public void add(Position vector) {
        add(vector.x, vector.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

