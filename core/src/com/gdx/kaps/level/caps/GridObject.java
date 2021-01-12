package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;

public interface GridObject extends Renderable {
    int x();
    int y();
    Color color();
    GridObject linked();

    boolean isLinked();

    boolean isInGrid();
    boolean collidesPile();
    boolean isAtValidEmplacement();

    boolean dip();
    void flip();
    void unlink();

    int linkedX();
    int linkedY();

    void render(int x, int y);
}
