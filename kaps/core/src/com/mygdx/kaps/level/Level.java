package com.mygdx.kaps.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.kaps.Kaps;
import com.mygdx.kaps.Renderer;
import com.mygdx.kaps.level.board.Board;
import com.mygdx.kaps.level.board.caps.CapsColor;
import com.mygdx.kaps.level.board.caps.Gelule;
import com.mygdx.kaps.utils.Timer;

import java.awt.Color;
import java.util.ArrayList;

public class Level {
  private final ArrayList<CapsColor> colors;
  private final Timer flipSpeed;
  private final Board board;
  private Gelule next, hold;
  private boolean moveMode;
  private boolean canHold;

  public Level(int x, int y) {
    board = new Board(x, y);
    colors = CapsColor.randomSet(3);
    next = new Gelule(colors);
    hold = null;
    canHold = true;
    flipSpeed = new Timer(Kaps.options.flipSpeed());
  }

  // getters
  public Board board() {
    return board;
  }

  // setters
  public void disableMoveMode() {
    moveMode = false;
  }

  // moves
  private void dipGelule() {
    board.moveDown();
  }
  private void dropGelule() {
    board.dropGelule();
  }
  private void holdGelule() {
    if (!canHold) return;
    canHold = false;

    if (hold == null) {
      hold = new Gelule(board.gelule(0));
      board.removeGelule();
    }
    else board.swapColors(board.gelule(0), hold);
  }
  private void loadNextGelule() {
    board.loadNewGelule(new Gelule(next, board.xMiddle(), board.topLineIndex()));
    next = new Gelule(colors);
  }

  // controller
  public void control() {
    if (board.hasNoGelule()) return;

    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) dropGelule();
    if (Gdx.input.isKeyJustPressed(Input.Keys.H)) holdGelule();
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) board.moveLeft();
    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) board.moveRight();
    if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) dipGelule();
    if (Gdx.input.isKeyPressed(Input.Keys.UP) && flipSpeed.isExceeded()) {
      board.flipGelule();
      flipSpeed.reset();
    }

    if (Gdx.input.isTouched()) {
      Vector2 touchPos = new Vector2();
      touchPos.set(Gdx.input.getX(), Gdx.input.getY());
      if (!Kaps.dimensions.board.contains(touchPos.x, touchPos.y)) return;

      if (!moveMode && Gdx.input.getDeltaX() != 0 && Gdx.input.getDeltaY() != 0) moveMode = true;
      board.selectObject(touchPos.x, touchPos.y);
      if (moveMode && board.hasSomethingSelected()) board.selection().followMouse(touchPos.x, touchPos.y);
    }
  }

  // update
  public void update() {
    board.update();
    if (board.hasNoGelule()) {
      loadNextGelule();
      canHold = true;
    }
  }

  // render
  public void render() {
    // background
    Kaps.renderer.renderRectangle(Kaps.dimensions.boardPanel,  new Color(150, 160, 180));
    Kaps.renderer.renderRectangle(Kaps.dimensions.sidePanel,   new Color(120, 130, 150));
    Kaps.renderer.renderRectangle(Kaps.dimensions.bottomPanel, new Color(100, 110, 120));
    // next
    Kaps.renderer.renderRectangle(Kaps.dimensions.nextBox, new Color(100, 110, 130));
    next.render(Kaps.dimensions.nextGelule);
    Kaps.renderer.drawText("NEXT", Kaps.dimensions.sidePanelWithPadding(), Kaps.dimensions.nextBox.y + Kaps.dimensions.nextBox.height - Renderer.parameter.size);
    // hold
    Kaps.renderer.renderRectangle(Kaps.dimensions.holdBox, new Color(90,  100, 120));
    if (hold != null) hold.render(Kaps.dimensions.holdGelule);
    Kaps.renderer.drawText("HOLD", Kaps.dimensions.sidePanelWithPadding(), Kaps.dimensions.holdBox.y + Kaps.dimensions.holdBox.height - Renderer.parameter.size);
    // board
    board.render();
  }
}
