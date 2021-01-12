package com.mygdx.kaps.utils;

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
  public void set(int x, int y) {
    setX(x);
    setY(y);
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
    this.x += x;
  }
  public void addY(int y) {
    this.y += y;
  }

  public Coords shifted(int x, int y) {
    return new Coords(this.x + x, this.y + y);
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }
}
