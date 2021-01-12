package com.gdx.kaps.level.caps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.Grid;
import com.gdx.kaps.level.Level;

import java.util.Objects;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;

class Caps implements GridObject, Renderable {
    private final Position position;  // IMPL: must be updated if set in grid
    private final Color color;
    private final Grid grid;
    private Sprite sprite;
    private Look look;

    public Caps(int x, int y, Look look, Level lvl) {
        Objects.requireNonNull(look);
        Objects.requireNonNull(lvl);
        this.look = look;
        grid = lvl.grid();
        this.color = Color.random(lvl.colors());
        position = new Position(x, y);
        updateTexture();
    }

    public Caps(Caps caps) {
        Objects.requireNonNull(caps);
        position = caps.position.shifted(0, 0);
        sprite = caps.sprite;
        color = caps.color;
        grid = caps.grid;
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
    public GridObject linked() {
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

    // movement
    boolean move(Look look) {
        if (shifted(look).isAtValidEmplacement()) {
            position.add(look.vector());
            return true;
        }
        return false;
    }

    @Override
    public void flip() {
        look = look.flipped();
        updateTexture();
    }

    @Override
    public void unlink() {
        look = Look.NONE;
        updateTexture();
    }

    @Override
    public boolean dip() {
        if (isLinked()) return false;
        return move(Look.DOWN);
    }

    @Override
    public boolean isLinked() {
        return look != Look.NONE;
    }

    void linkTo(Caps caps) {
        Objects.requireNonNull(caps);
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
