package com.gdx.kaps.level.caps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.Grid;

import java.util.Objects;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;

public class Caps implements GridObject, Renderable {
    private final Position position;  // IMPL: must be updated if set in grid
    private final Color color;
    private Sprite sprite;
    private Look look;

    public Caps(int x, int y, Look look, Color color) {
        Objects.requireNonNull(look);
        Objects.requireNonNull(color);
        this.look = look;
        this.color = color;
        position = new Position(x, y);
        updateTexture();
    }

    public Caps(Caps caps) {
        Objects.requireNonNull(caps);
        position = caps.position.shifted(0, 0);
        color = caps.color;
        sprite = caps.sprite;
        look = caps.look;
    }

    // getters
    public int x() {
        return position.x();
    }

    public int y() {
        return position.y();
    }

    public Look look() {
        return look;
    }

    public Color color() {
        return color;
    }

    @Override
    public GridObject linked(Grid grid) {
        return grid.getLinked(this);
    }
    @Override
    public int linkedX() {
        return position.shifted(look.opposite().vector()).x();
    }
    @Override
    public int linkedY() {
        return position.shifted(look.opposite().vector()).y();
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

    // predicates
    @Override
    public boolean isInGrid(Grid grid) {
        Objects.requireNonNull(grid);
        return grid.isInGrid(position.x(), position.y());
    }

    @Override
    public boolean collidesPile(Grid grid) {
        Objects.requireNonNull(grid);
        return grid.collidesPile(position.x(), position.y());
    }

    @Override
    public boolean isAtValidEmplacement(Grid grid) {
        return isInGrid(grid) && !collidesPile(grid);
    }

    // movement
    boolean move(Look look, Grid grid) {
        if (shifted(look).isAtValidEmplacement(grid)) {
            position.add(look.vector());
            return true;
        }
        return false;
    }

    @Override
    public void flip(Grid grid) {
        look = look.flipped();
        updateTexture();
    }

    @Override
    public void unlink() {
        look = Look.NONE;
        updateTexture();
    }

    @Override
    public boolean dip(Grid grid) {
        if (isLinked()) return false;
        return move(Look.DOWN, grid);
    }

    @Override
    public boolean isLinked() {
        return look != Look.NONE;
    }

    void linkTo(Caps caps) {
        Objects.requireNonNull(caps);
        if (look == Look.NONE) {
            throw new IllegalStateException("Unlinked caps can't have a linked");
        }
        look = caps.look.opposite();
        position.set(caps.position);
        position.add(caps.look.opposite().vector());
        updateTexture();
    }

    public Caps copy() {
        return new Caps(this);
    }

    Caps shifted(Look look) {
        Objects.requireNonNull(look);
        var copy = copy();
        copy.position.add(look.vector());
        return copy;
    }

    void updateTexture() {
        sprite = new Sprite(new Texture("img/" + color.id() + "/caps/" + look + ".png"));
        sprite.flip(false, true);
    }

    @Override
    public String toString() {
        return "(" + position.x() + "," + position.y() + ")";
    }

    @Override
    public void render() {
        render(position.x(), position.y());
    }
}
