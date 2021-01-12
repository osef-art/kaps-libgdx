package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;

public interface GridObject extends Renderable {
    int x();
    int y();
    int linkedX();
    int linkedY();
    Color color();
    GridObject linked();

    boolean isLinked();
    boolean isInGrid();
    boolean collidesPile();
    boolean isAtValidEmplacement();

    void flip();
    void unlink();
    boolean dip();

    void render(int x, int y);
}
