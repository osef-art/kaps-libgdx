package com.gdx.kaps.contoller;

import com.badlogic.gdx.InputProcessor;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.time.Timer;

import java.util.HashMap;
import java.util.stream.IntStream;

public class LevelController implements InputProcessor {
    // TODO: handle mouse inputs
    private final HashMap<Integer, Boolean> pressed = new HashMap<>();
    private final static double UPDATE_SPEED = 75_000_000;
    private final Timer moveSpeed;
    private final Level level;

    public LevelController(Level lvl) {
        moveSpeed = new Timer(UPDATE_SPEED);
        level = lvl;
        IntStream.range(0, 100)
          .forEach(n -> pressed.put(n, false));
    }

    @Override
    public boolean keyDown (int keycode) {
        moveSpeed.reset();
        pressed.put(keycode, true);

        switch (keycode) {
            case 44: // P
                level.togglePause();
                break;
            case 45: // Q
                System.exit(0);
        }

        // SINGLE SHOTS
        if (level.isPaused()) return false;

        switch (keycode) {
            case 62: // SPACE
                level.dropGelule();
                break;
            case 36: // H
                level.hold();
                break;
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
        if (level.isPaused()) return;
        if (moveSpeed.resetIfExceeds()) {
            // CONTINUOUS INPUT
            if (pressed.get(21)) level.moveGeluleLeft();
            if (pressed.get(22)) level.moveGeluleRight();
            if (pressed.get(20)) level.dipGelule();
            if (pressed.get(19)) level.flipGelule();
        }
    }
}
