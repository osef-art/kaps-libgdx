package com.gdx.kaps.contoller;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;
import com.gdx.kaps.MainScreen;
import com.gdx.kaps.level.Level;

import java.util.HashMap;
import java.util.stream.IntStream;

public class InputHandler implements InputProcessor {
    static class Chrono {
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

    private final HashMap<Integer, Boolean> pressed = new HashMap<>();
    private final static double UPDATE_SPEED = 75_000_000;
    private final Chrono chrono = new Chrono();
    private final Level level;

    public InputHandler() {
        level = MainScreen.level;
        IntStream.range(0, 100)
          .forEach(n -> pressed.put(n, false));
    }

    @Override
    public boolean keyDown (int keycode) {
        chrono.reset();
        pressed.put(keycode, true);

        // SINGLE SHOT
        switch (keycode) {
            case 62: // SPACE
                level.dropGelule();
                break;
            case 36: // H
                break;
            case 45: // Q
                System.exit(0);
        }
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        pressed.put(keycode, false);
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        return false;
    }

    public void update() {
        if (chrono.exceeds(UPDATE_SPEED)) {
            chrono.reset();
            // CONTINUOUS INPUT
            if (pressed.get(21)) level.moveGeluleLeft();
            if (pressed.get(22)) level.moveGeluleRight();
            if (pressed.get(20)) level.dipGelule();
            if (pressed.get(19)) level.flipGelule();
        }
    }
}
