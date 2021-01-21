package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;

import static com.gdx.kaps.MainScreen.sra;

public class Gauge {
    private final int max;
    private int value;

    public Gauge(int value) {
        max = value;
    }

    public double ratio() {
        return (double) value / max;
    }

    public void increase() {
        value = Math.min(value + 1, max);
    }

    public void reset() {
        value = 0;
    }

    public void render(float x, float y, float width, float height, Color back, Color main) {
        sra.drawRect(x, y, width, height, back);
        sra.drawRect(x, y, (float) (width * ratio()), height, main);
    }
}
