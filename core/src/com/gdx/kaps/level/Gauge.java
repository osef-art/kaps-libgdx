package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;

import static com.gdx.kaps.MainScreen.sra;

public class Gauge {
    private final int max;
    private int value;

    public Gauge(int value) {
        max = value;
    }

    public int max() {
        return max;
    }

    public int value() {
        return value;
    }

    public float ratio() {
        return (float) value / max;
    }

    public void increase() {
        value = Math.min(value + 1, max);
    }

    public void reset() {
        value = 0;
    }

    public void render(float x, float y, float width, float height, Color back, Color main) {
        sra.drawRect(x + height/2, y, width - height, height, back);
        sra.drawCircle(x + width - height/2, y + height/2, height/2, back);

        sra.drawRect(x + height/2, y, width * ratio(), height, main);
        sra.drawCircle(x + height/2, y + height/2, height/2, main);
        sra.drawCircle(x + (width - height) * ratio() + height/2, y + height/2, height/2, main);
    }
}
