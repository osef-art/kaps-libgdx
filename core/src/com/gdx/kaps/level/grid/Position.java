package com.gdx.kaps.level.grid;

public class Position {
    private int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void add(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void add(Position vector) {
        add(vector.x, vector.y);
    }

    public void set(Position pos) {
        set(pos.x, pos.y);
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position shifted(int x, int y) {
        return new Position(this.x + x, this.y + y);
    }

    public Position shifted(Position vector) {
        return shifted(vector.x, vector.y);
    }
}
