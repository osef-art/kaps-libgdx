package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.Grid;

// TODO: find a better name >.<
public interface GridObject extends Renderable {
    int x();
    int y();
    Color color();
    GridObject linked(Grid grid);

    boolean isLinked();

    boolean isInGrid(Grid grid);
    boolean collidesPile(Grid grid);
    boolean isAtValidEmplacement(Grid grid);

    boolean dip(Grid grid);
    void flip(Grid grid);
    void unlink();

    int linkedX();
    int linkedY();

    void render(int x, int y);
}
