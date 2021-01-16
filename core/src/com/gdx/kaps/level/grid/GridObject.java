package com.gdx.kaps.level.grid;

import com.gdx.kaps.Renderable;

import java.util.Optional;

public interface GridObject extends Renderable {
    int x();
    int y();
    Color color();

    Optional<GridObject> linked();
    GridObject unlinked();

    boolean isGerm();
    boolean canDip(Grid grid);
    void dipIfPossible(Grid grid);

    void render(int x, int y);
    default void render() {
        render(x(), y());
    }
}
