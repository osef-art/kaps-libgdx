package com.mygdx.kaps.utils;

public class Vector {
  private double x, y;

  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }
  public Vector() {
    this(0, 0);
  }

  public double x() {
    return x;
  }
  public double y() {
    return y;
  }
  public void set(Vector v) {
    set(v.x, v.y);
  }
  public void set(double x, double y) {
    setX(x);
    setY(y);
  }
  public void setX(double x) {
    this.x = x;
  }
  public void setY(double y) {
    this.y = y;
  }
  public void mult(double x, double y) {
    multX(x);
    multY(y);
  }
  public void multX(double x) {
    this.x *= x;
  }
  public void multY(double y) {
    this.y *= y;
  }

  @Override
  public String toString() {
    return "( " + x + " , " + y + " )";
  }
}
