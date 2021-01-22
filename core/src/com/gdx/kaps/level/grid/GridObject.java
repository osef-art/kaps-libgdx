package com.gdx.kaps.level.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.renderer.Zone;

import static com.gdx.kaps.MainScreen.*;
import static java.util.Objects.requireNonNull;

public abstract class GridObject implements GridObjectInterface {
    private final Position position;
    private final Color color;
    private Sprite sprite;

    public GridObject(int x, int y, Color color) {
        requireNonNull(color);
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

    public Position pos() {
        return position;
    }

    // update

    protected void updateSprite(String path) {
        sprite = new Sprite(new Texture(path));
        sprite.flip(false, true);
    }

    @Override
    public void render() {
        render(x(), y());
    }

    @Override
    public void render(int x, int y) {
        render(
          dim.gridMargin + x * dim.get(Zone.TILE).height,
          dim.topTile(y),
          dim.get(Zone.TILE).width,
          dim.get(Zone.TILE).height
        );
    }

    public void render(float x, float y, float width, float height) {
        render(x, y, width, height, 1);
    }

    public void render(float x, float y, float width, float height, float alpha) {

        spra.render(sprite, x, y, width, height, alpha);
    }
}
