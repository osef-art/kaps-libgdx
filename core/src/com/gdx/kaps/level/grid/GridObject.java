package com.gdx.kaps.level.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Objects;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;

public abstract class GridObject implements GridObjectInterface {
    private final Color color;
    final Position position; // IMPL: find a way to make it private (so close..)
    private Sprite sprite;

    public GridObject(int x, int y, Color color) {
        Objects.requireNonNull(color);
        this.position = new Position(x, y);
        this.color = color;
    }


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

    void move(Look look) {
        position.add(look.vector());
    }

    // update

    protected void updateSprite(String path) {
        sprite = new Sprite(new Texture(path));
        sprite.flip(false, true);
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
