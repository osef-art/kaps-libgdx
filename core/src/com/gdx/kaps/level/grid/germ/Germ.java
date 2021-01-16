package com.gdx.kaps.level.grid.germ;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.Position;

import java.util.Optional;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public abstract class Germ implements GridObject {
    // TODO: abstract class for shared fiels ?
    private int health;
    private final int maxHP;
    private final Color color;
    private Sprite sprite;
    final Position position;
    final Grid grid;

    Germ(int x, int y, Color color, int HP, Grid grid) {
        requireNonNull(color);
        requireNonNull(grid);
        if (HP < 0) throw new IllegalArgumentException("Germ can't have a negative health (" + HP + ")");

        position = new Position(x, y);
        this.color = color;
        this.grid = grid;
        health = HP;
        maxHP = HP;
    }

    @Override
    public int x() {
        return position.x();
    }

    @Override
    public int y() {
        return position.x();
    }

    @Override
    public Color color() {
        return color;
    }

    @Override
    public Optional<GridObject> linked() {
        return Optional.empty();
    }

    @Override
    public GridObject unlinked() {
        return this;
    }

    @Override
    public boolean canDip() {
        return false;
    }

    @Override
    public void dipIfPossible() {
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

