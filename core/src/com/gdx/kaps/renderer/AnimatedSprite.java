package com.gdx.kaps.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.time.Timer;

import java.nio.file.Path;

import static com.gdx.kaps.MainScreen.batch;

public class AnimatedSprite implements Renderable {
    private final Timer updateTimer;
    private final int nbFrames;
    private Sprite sprite;
    private Path path;
    private int frame;

    public AnimatedSprite(String path, int frames, double speed) {
        updateTimer = new Timer(speed);
        nbFrames = frames;
        updatePath(path);
        updateSprite();
    }

    public void updatePath(String path) {
        this.path = Path.of(path);
    }

    public void updateSprite() {
        sprite = new Sprite(new Texture(path.toString() + frame + ".png"));
        sprite.flip(false, true);
    }

    @Override
    public void update() {
        if (updateTimer.resetIfExceeds()) frame = (frame + 1) % nbFrames;
        updateSprite();
    }

    @Override
    public void render() {
        sprite.draw(batch);
    }

    @Override
    public void render(float x, float y, float width, float height) {
        batch.begin();
        batch.draw(sprite, x, y, width, height);
        batch.end();
    }
}
