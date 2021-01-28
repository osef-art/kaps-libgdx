package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class Caps extends GridObject {
    private boolean destroyed;

    Caps(int x, int y, Color color) {
        super(x, y, color);
        updateSprite();
    }

    Caps(Caps caps) {
        super(caps.x(), caps.y(), caps.color());
        requireNonNull(caps);
        destroyed = false;
    }

    // getters

    @Override
    public Optional<GridObject> linked() {
        return Optional.empty();
    }

    private Caps copy() {
        return new Caps(this);
    }

    private Caps shifted(Look look) {
        requireNonNull(look);
        var copy = copy();
        copy.move(look);
        return copy;
    }

    @Override
    public Caps unlinked() {
        return this;
    }

    @Override
    public int points() {
        return 20;
    }

    // predicates

    @Override
    public boolean isGerm() {
        return false;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isInGrid(Grid grid) {
        requireNonNull(grid);
        return grid.isInGrid(x(), y());
    }

    public boolean collidesPile(Grid grid) {
        requireNonNull(grid);
        return grid.collidesPile(x(), y());
    }

    public boolean isAtValidEmplacement(Grid grid) {
        return isInGrid(grid) && !collidesPile(grid);
    }

    private boolean canMove(Look look, Grid grid) {
        return shifted(look).isAtValidEmplacement(grid);
    }

    // movement

    void move(Look look) {
        requireNonNull(look);
        pos().add(look.vector());
    }

    private void moveIfPossible(Look look, Grid grid) {
        if (!canMove(look, grid)) return;
        move(look);
    }

    @Override
    public boolean canDip(Grid grid) {
        return canMove(Look.DOWN, grid);
    }

    @Override
    public void dipIfPossible(Grid grid) {
        moveIfPossible(Look.DOWN, grid);
    }

    @Override
    public void hit() {
        destroyed = true;
    }

    @Override
    public void paint(Color color) {
        super.paint(color);
        updateSprite();
    }

    private void updateSprite() {
        super.updateSprite(
          "android/assets/img/" + color().id() +
            "/caps/" + Look.NONE + ".png"
        );
    }

    // update

    @Override
    public void update() {
    }

    public String toString() {
        return "(" + x() + "," + y() + ")";
    }
}
