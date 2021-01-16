package com.gdx.kaps.time;

public class Timer {
    private final Chrono chrono;
    private double limit;

    public Timer(double limit) {
        chrono = new Chrono();
        this.limit = limit;
    }

    public boolean resetIfExceeds() {
        if (chrono.exceeds(limit)) {
            chrono.reset();
            return true;
        }
        return false;
    }

    public void reset() {
        chrono.reset();
    }

    public void updateLimit(int newLimit) {
        limit = newLimit;
    }

    public double ratio() {
        return chrono.value() / limit;
    }
}
