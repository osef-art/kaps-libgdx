package com.gdx.kaps.time;

import com.badlogic.gdx.utils.TimeUtils;

class Chrono {
    private long start;

    public Chrono() {
        reset();
    }

    public boolean exceeds(double value) {
        return value() > value;
    }

    public double value() {
        return TimeUtils.nanoTime() - start;
    }

    public void reset() {
        start = TimeUtils.nanoTime();
    }
}
