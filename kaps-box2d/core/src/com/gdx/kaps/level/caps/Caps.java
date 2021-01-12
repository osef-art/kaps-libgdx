package com.gdx.kaps.level.caps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.Grid;

import java.util.Objects;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;

public class Caps implements GridObject, Renderable {
    private final Position position;
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

    /**
     * @return the supposed position of the caps it's linked to.
     * returns its own position if unlinked.
     */
    public Position linkedPosition() {
        return position.shifted(look.opposite().vector());
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

    public void unlink() {
        look = Look.NONE;
        updateTexture();
    }

    public boolean dip(Grid grid) {
        return move(Look.DOWN, grid);
    }

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

    private Caps copy() {
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
        batch.begin();
        batch.draw(
          sprite,
          dim.boardMargin + position.x() * dim.tile.height,
          dim.topTile(position.y()),
          dim.tile.width,
          dim.tile.height
        );
        batch.end();
    }
}
