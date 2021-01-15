package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;

public interface GridObject extends Renderable {
    // TODO: Caps interface that extends GridObject. Linked and UnlinkedCaps extend Caps
    int x();
    int y();
    Color color();

    boolean isInGrid();
    boolean collidesPile();
    boolean isAtValidEmplacement();

    void render(int x, int y);
}
