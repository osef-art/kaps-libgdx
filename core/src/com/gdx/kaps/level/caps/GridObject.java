package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.Grid;

// TODO: find a better name >.<
public interface GridObject extends Renderable {
    int x();
    int y();

    Color color();
    Position linkedPosition();

    boolean isLinked();
    boolean canDip(Grid grid);
    boolean isInGrid(Grid grid);
    boolean collidesPile(Grid grid);
    boolean isAtValidEmplacement(Grid grid);

    boolean dip(Grid grid);
    void flip(Grid grid);
    void unlink();
}
