package com.gdx.kaps.level.caps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.level.Grid;

import java.util.Optional;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public class Caps implements GridObject {
    final Position position;
    private final Sprite sprite;
    private final Color color;
    final Grid grid;

    Caps(int x, int y, Color color, Grid grid) {
        requireNonNull(color);
        requireNonNull(grid);
        this.grid = grid;
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
        grid = caps.grid;
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

    public Optional<Caps> linked() {
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

    public Caps unlinked() {
        return this;
    }

    // predicates

    @Override
    public boolean isInGrid() {
        return grid.isInGrid(position.x(), position.y());
    }

    @Override
    public boolean collidesPile() {
        return grid.collidesPile(position.x(), position.y());
    }

    @Override
    public boolean isAtValidEmplacement() {
        return isInGrid() && !collidesPile();
    }

    private boolean canMove(Look look) {
        return shifted(look).isAtValidEmplacement();
    }

    // movement

    void move(Look look) {
        position.add(look.vector());
    }

    private boolean moveIfPossible(Look look) {
        if (!canMove(look)) return false;
        move(look);
        return true;
    }
    public boolean canDip() {
        return canMove(Look.DOWN);
    }

    public boolean dipIfPossible() {
        return moveIfPossible(Look.DOWN);
    }

    // update

    @Override
    public String toString() {
        return "(" + position.x() + "," + position.y() + ")";
    }

    @Override
    public void render() {
        render(position.x(), position.y());
    }

    @Override
    public void render(int x, int y) {
        batch.begin();
        batch.draw(
          sprite,
          dim.boardMargin + x * dim.tile.height,
          dim.topTile(y),
          dim.tile.width,
          dim.tile.height
        );
        batch.end();
    }

}
