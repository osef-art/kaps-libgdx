package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;

import static com.gdx.kaps.MainScreen.sra;

public class Gauge {
    private final int max;
    private int value;

    public Gauge(int value) {
        max = value;
    }

    public Gauge(int value, int max) {
        this.value = value;
        this.max = max;
    }

    // getters

    public int max() {
        return max;
    }

    public int value() {
        return value;
    }

    public float ratio() {
        return (float) value / max;
    }

    // predicates

    public boolean isEmpty() {
        return value == 0;
    }

    public boolean isFull() {
        return value >= max;
    }

    // operations

    public void fill() {
        value = max;
    }

    public void empty() {
        value = 0;
    }

    public void increase() {
        value = Math.min(value + 1, max);
    }

    public void decrease() {
        value = Math.max(value - 1, 0);
    }

    public void reset() {
        value = 0;
    }

    public void render(float x, float y, float width, float height, Color back, Color main) {
        sra.drawRect(x + height/2, y, width - height, height, back);
        sra.drawCircle(x + width - height/2, y + height/2, height/2, back);

        sra.drawRect(x + height/2, y, (width - height) * ratio(), height, main);
        sra.drawCircle(x + height/2, y + height/2, height/2, main);
        sra.drawCircle(x + height/2 + (width - height) * ratio(), y + height/2, height/2, main);
    }
}
