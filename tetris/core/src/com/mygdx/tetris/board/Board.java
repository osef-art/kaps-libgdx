package com.mygdx.tetris.board;

import com.mygdx.tetris.board.shape.Shape;
import com.mygdx.tetris.board.shape.block.Block;
import com.mygdx.tetris.board.shape.block.Coords;

import java.util.ArrayList;

public class Board extends ArrayList<Line> {
  private final Coords dimensions;
  private Shape shape, next;

  public Board(int width, int height) {
    dimensions = new Coords(width, height);
    for (int i = 0; i < height; i++) addEmptyLine();
    next = Shape.randomShape();
    loadNewShape();
  }

  // getters
  public int width() {
    return dimensions.x();
  }
  public int height() {
    return dimensions.y();
  }
  private int xCenter() {
    return dimensions.x()/2;
  }
  public Shape next() {
    return next;
  }
  public Shape shape() {
    return shape;
  }
  public Shape preview() {
    Shape preview = shape.copy();
    while (!preview.shift(0, -1).collidesBottom() && !preview.shift(0, -1).collidesPile(this)) preview.dip();
    return preview;
  }

  public int topLine() {
    return size() - 1;
  }
  private void addEmptyLine() {
    add(new Line(dimensions.x()));
  }


  // moves
  private void doNothing() { }
  public void moveShapeLeft() {
    if (!shape.shift(-1, 0).collidesLeftWall() && !shape.shift(-1, 0).collidesPile(this)) shape.moveLeft();
  }
  public void moveShapeRight() {
    if (!shape.shift(1, 0).collidesRightWall(this) && !shape.shift(1, 0).collidesPile(this)) shape.moveRight();
  }
  public void dropShape() {
    while (dipShape()) doNothing();
  }
  public boolean dipShape() {
    if (!shape.shift(0, -1).collidesBottom() && !shape.shift(0, -1).collidesPile(this)) {
      shape.dip();
      return true;
    }
    add();
    return false;
  }
  public void flipShape() {
    shape.flip();
    while (shape.collidesLeftWall()) shape.moveRight();
    while (shape.collidesRightWall(this)) shape.moveLeft();
    while (shape.collidesTop(this)) shape.dip();
    while (shape.collidesBottom()) shape.add(0, 1);
    if (shape.collidesPile(this)) for (int i = 0; i < 3; i++) shape.flip();
  }

  // bon
  public void loadNewShape() {
    shape = next;
    next = Shape.randomShape();
    for (Block block : shape) block.add(xCenter(), topLine());
  }
  public void add() {
    for (Block block : shape) get(block.y()).set(block.x(), block.type());
    loadNewShape();
    while (checkForLines()) doNothing();
  }

  private boolean checkForLines() {
    for (Line line : this) {
      if (line.isFilled()) {
        remove(line);
        addEmptyLine();
        return true;
      }
    }
    return false;
  }

  public void draw() {
    for (int y = 0; y < size(); y++) get(y).draw(y);
    shape.draw();
    preview().draw((float) 0.5);
  }
}
