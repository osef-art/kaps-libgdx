package com.mygdx.stickrush;

public class Position {
    private final Point point;

    public Position(int x, int y) {
        point = new Point(x, y);
    }

    @Override
    public String toString() {
        return point.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return getX() == position.getX() &&
                 getY() == position.getY();
    }

    public int getX() {
        return point.getX();
    }

    public int getY() {
        return point.getY();
    }
}
