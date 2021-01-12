package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.Grid;

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
    void unlink(Grid grid);

    int linkedX();
    int linkedY();

    void render(int x, int y);
}
