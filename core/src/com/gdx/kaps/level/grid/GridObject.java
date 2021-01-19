package com.gdx.kaps.level.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;
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

    public void setSpriteAlpha(float alpha) {
        sprite.setAlpha(alpha);
    }

    // update

    protected void updateSprite(int x, int y, String path) {
        sprite = new Sprite(new Texture(path));
        sprite.setX(dim.gridMargin + x * dim.tile.height);
        sprite.setY(dim.topTile(y));
        // FIXME: update sprite size
        sprite.flip(false, true);
    }

    @Override
    public void render(int x, int y) {
        render(
          dim.gridMargin + x * dim.tile.height,
          dim.topTile(y),
          dim.tile.width,
          dim.tile.height
        );
    }

    public void render(float x, float y, float width, float height) {
        batch.begin();
        // TODO: use for certain cases
        //  batch.draw(sprite, x, y, width, height);
        sprite.draw(batch);
        batch.end();
    }
}
