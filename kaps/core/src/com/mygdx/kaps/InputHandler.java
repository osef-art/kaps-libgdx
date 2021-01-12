package com.mygdx.kaps;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.kaps.level.board.Board;

public class InputHandler implements InputProcessor {
  private final Vector2 lastClick = new Vector2();
  private final Board board;

  public InputHandler() {
    board = Kaps.level.board();
  }

  @Override
  public boolean keyDown (int keycode) {
    return false;
  }

  @Override
  public boolean keyUp (int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped (char character) {
    return false;
  }

  @Override
  public boolean touchDown (int x, int y, int pointer, int button) {
    lastClick.set(x, y);
    return false;
  }

  @Override
  public boolean touchUp (int x, int y, int pointer, int button) {
    if (board.geluleIsSelected() && lastClick.epsilonEquals(x, y, 5)) board.flipGelule();
    board.deselectGelule();
    Kaps.level.disableMoveMode();
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
}
