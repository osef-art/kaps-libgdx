package com.mygdx.stickrush;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

import static com.mygdx.stickrush.Game.*;

public class InputHandler implements InputProcessor {
    private final Vector2 lastClick = new Vector2();

    @Override
    public boolean keyDown(int keycode) {
      return false;
    }

    @Override
    public boolean keyUp(int keycode) {
      return false;
    }

    @Override
    public boolean keyTyped(char character) {
      return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        // set ortho
        y = (int) dim.window.height - y;
        lastClick.set(x, y);
        cursor.enable();
        cursor.update(x, y);

        if (dim.grid.contains(lastClick)) {
            level.getGrid().select(
                (int) (x / dim.tile.width),
                (int) ((y - dim.summons.height)  / dim.tile.height)
            );
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        cursor.disable();
        level.getGrid().unselect();
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        y = (int) dim.window.height - y;
        Vector2 acc = new Vector2(x, y).sub(lastClick);
        cursor.update(x, y);

        if (level.getGrid().hasSelection()) {
            if (dim.tile.width/2 < acc.x) {
                level.getGrid().swapRight();
            } else if (acc.x < -dim.tile.width/2) {
                level.getGrid().swapLeft();
            } else if (dim.tile.height/2 < acc.y) {
                level.getGrid().swapUp();
            } else if (acc.y < -dim.tile.height/2 ) {
                level.getGrid().swapDown();
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
