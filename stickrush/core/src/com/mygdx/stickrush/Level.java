package com.mygdx.stickrush;

import com.badlogic.gdx.utils.TimeUtils;

import static com.mygdx.stickrush.Game.*;

public class Level {
    private final static long updateSpeed = 100_000_000;
    private int multiplier = 1, score = 0;
    private final Grid grid;
    private final Hero hero;
    private long lastUpdate;

    public Level(Grid grid) {
        FrameSet frames = new FrameSet("blank/stand/stand", 16);
        this.hero = new Hero(frames, dim.hero);
        this.grid = grid;
    }

    public void update() {
        if (TimeUtils.nanoTime() - lastUpdate > updateSpeed) {
            lastUpdate = TimeUtils.nanoTime();

            if (grid.isFilled()) grid.popAll();
            else grid.dipAll();
        }
    }

    public Hero getHero() {
    return hero;
    }

    public Grid getGrid() {
    return grid;
  }

    public int getScore() {
        return score;
    }

    public void incerementScore() {
        score += 10 * multiplier;
    }
}
