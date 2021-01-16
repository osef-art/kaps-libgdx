package com.gdx.kaps.level.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Optional;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public class Caps implements GridObject {
    private final Sprite sprite;
    private final Color color;
    final Position position;

    Caps(int x, int y, Color color) {
        requireNonNull(color);
        this.color = color;
        position = new Position(x, y);
        sprite = new Sprite(new Texture("img/" + color.id() + "/caps/" + Look.NONE + ".png"));
        sprite.flip(false, true);
    }

    Caps(Caps caps) {
        requireNonNull(caps);
        position = caps.position.shifted(0, 0);
        sprite = caps.sprite;
        color = caps.color;
    }

    // getters

    @Override
    public int x() {
        return position.x();
    }

    @Override
    public int y() {
        return position.y();
    }

    @Override
    public Color color() {
        return color;
    }

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
        copy.position.add(look.vector());
        return copy;
    }

    @Override
    public Caps unlinked() {
        return this;
    }

    // predicates

    @Override
    public boolean isGerm() {
        return false;
    }

    public boolean isInGrid(Grid grid) {
        requireNonNull(grid);
        return grid.isInGrid(position.x(), position.y());
    }

    public boolean collidesPile(Grid grid) {
        requireNonNull(grid);
        return grid.collidesPile(position.x(), position.y());
    }

    public boolean isAtValidEmplacement(Grid grid) {
        return isInGrid(grid) && !collidesPile(grid);
    }

    private boolean canMove(Look look, Grid grid) {
        return shifted(look).isAtValidEmplacement(grid);
    }

    // movement

    void move(Look look) {
        position.add(look.vector());
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

    // update

    public String toString() {
        return "(" + position.x() + "," + position.y() + ")";
    }

    @Override
    public void render(int x, int y) {
        batch.begin();
        batch.draw(
          sprite,
          dim.gridMargin + x * dim.tile.height,
          dim.topTile(y),
          dim.tile.width,
          dim.tile.height
        );
        batch.end();
    }
}
