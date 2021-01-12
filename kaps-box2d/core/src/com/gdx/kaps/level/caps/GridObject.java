package com.gdx.kaps.level.caps;

import com.gdx.kaps.level.Grid;

// TODO: find a better name >.<
public interface GridObject {
    boolean isAtValidEmplacement(Grid grid);
    boolean isInGrid(Grid grid);
    boolean collidesPile(Grid grid);
    void flip(Grid grid);
}
