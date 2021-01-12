package com.mygdx.tetris.board.shape.block;

public class Coords {
  private int x, y;

  public Coords(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int x() {
    return x;
  }
  public int y() {
    return y;
  }

  public void setX(int x) {
    this.x = x;
  }
  public void setY(int y) {
    this.y = y;
  }
  public void add(int x, int y) {
    addX(x);
    addY(y);
  }
  public void addX(int x) {
    setX(this.x + x);
  }
  public void addY(int y) {
    setY(this.y + y);
  }

}
