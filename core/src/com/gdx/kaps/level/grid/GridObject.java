package com.gdx.kaps.level.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.gdx.kaps.SoundStream;

import static com.gdx.kaps.MainScreen.spra;
import static java.util.Objects.requireNonNull;

public abstract class GridObject implements GridObjectInterface {
    private final SoundStream attacks = new SoundStream();
    private final Position position;
    private Sprite sprite;
    private Color color;

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

    public void paint(Color color) {
        this.color = color;
    }

    @Override
    public void triggerOnDeath(Grid grid) {
    }

    public void playSound(String path) {
        attacks.play(path);
    }

    // update

    protected void updateSprite(String path) {
        sprite = new Sprite(new Texture(path));
        sprite.flip(false, true);
    }

    public void render(float x, float y, float width, float height) {
        render(x, y, width, height, 1);
    }

    public void render(Rectangle rect, float alpha) {
        render(rect.x, rect.y, rect.width, rect.height, alpha);
    }

    private void render(float x, float y, float width, float height, float alpha) {
        spra.render(sprite, x, y, width, height, alpha);
    }
}
