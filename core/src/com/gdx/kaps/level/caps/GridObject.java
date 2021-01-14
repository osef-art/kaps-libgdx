package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;

import java.util.Optional;

public interface GridObject extends Renderable {
    int x();
    int y();
    Color color();
    Optional<GridObject> linked();

    boolean isInGrid();
    boolean collidesPile();
    boolean isAtValidEmplacement();

    boolean dipIfPossible();

    void render(int x, int y);

    Caps unlinked();
}
