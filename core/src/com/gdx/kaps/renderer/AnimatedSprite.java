package com.gdx.kaps.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.time.Timer;

import java.nio.file.Path;
import java.util.Random;

import static com.gdx.kaps.MainScreen.spra;

public class AnimatedSprite implements Animated, NonStatic {
    private final Timer updateTimer;
    private final Sprite[] sprites;
    private final boolean looping;
    private final int nbFrames;
    private Path path;
    private int frame;

    public AnimatedSprite(String path, int frames, double speed) {
        this(path, new Random().nextInt(frames), frames, speed, true);
    }

    public AnimatedSprite(String path, int startingFrame, int frames, double speed, boolean looping) {
        this.looping = looping;
        updateTimer = new Timer(speed);
        sprites = new Sprite[frames];
        frame = startingFrame;
        nbFrames = frames;

        updatePath(path);
    }

    public static AnimatedSprite oneShot(String path, int frame, double speed) {
        return new AnimatedSprite(path, 0, frame, speed, false);
    }

    private Sprite currentSprite() {
        return sprites[frame];
    }

    public void updatePath(String path) {
        this.path = Path.of(path);
        updateSprites();
    }

    private void updateSprites() {
        for (int i = 0; i < nbFrames; i++) {
            var sprite = new Sprite(new Texture(path.toString() + i + ".png"));
            sprite.flip(false, true);
            sprites[i] = sprite;
        }
    }

    @Override
    public void update() {
        if (frame == nbFrames -1 && !looping) return;
        if (updateTimer.resetIfExceeds()) frame = (frame + 1) % nbFrames;
    }

    @Override
    public void render() {
        spra.render(currentSprite());
    }

    @Override
    public void render(float x, float y, float width, float height) {
        spra.render(currentSprite(), x, y, width, height);
    }

    public boolean isOver() {
        return frame == nbFrames - 1 && updateTimer.isExceeded();
    }
}
