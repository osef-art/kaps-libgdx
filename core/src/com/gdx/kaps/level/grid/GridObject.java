package com.gdx.kaps.level.grid;

import com.gdx.kaps.Renderable;

import java.util.Optional;

public interface GridObject extends Renderable {
    // TODO: Caps interface that extends GridObject.
    //  Linked and UnlinkedCaps extend Caps
    int x();
    int y();
    Color color();

    Optional<GridObject> linked();
    GridObject unlinked();

    boolean canDip(Grid grid);
    void dipIfPossible(Grid grid);

    default void render() {
        render(x(), y());
    }
    void render(int x, int y);
}
