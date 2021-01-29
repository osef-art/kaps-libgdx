package com.gdx.kaps.level.grid;

import com.gdx.kaps.renderer.Animated;
import com.gdx.kaps.renderer.NonStatic;

import java.util.Optional;

public interface GridObjectInterface extends Animated, NonStatic {
    int x();
    int y();
    Color color();

    Optional<GridObject> linked();
    GridObject unlinked();
    int points();
    int mana();

    boolean isGerm();

    boolean isDestroyed();
    boolean canDip(Grid grid);

    void dipIfPossible(Grid grid);
    void hit();

    void render(int x, int y);
    default void render(float x, float y, float width, float height) {
        render(x(), y());
    }
}
