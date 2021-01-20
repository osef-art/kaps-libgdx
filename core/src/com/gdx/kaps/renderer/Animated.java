package com.gdx.kaps.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.time.Timer;

import java.nio.file.Path;

public abstract class Animated {
    private Sprite sprite;
    private final Path path;
    private final Timer updateTimer;
    private final int nbFrames;
    private int frame;

    public Animated(String path, int frames, double speed) {
        updateTimer = new Timer(speed);
        this.path = Path.of(path);
        nbFrames = frames;
        updateSprite();
    }

    public void update() {
        if (updateTimer.resetIfExceeds()) frame = (frame + 1) % nbFrames;
        updateSprite();
    }

    void updateSprite() {
        sprite = new Sprite(new Texture(path.toString() + frame + ".png"));
        sprite.flip(false, true);
    }

    public Sprite sprite() {
        return sprite;
    }
}
