package com.mygdx.tetris;

public class Timer extends Chrono {
  private double limit;

  public Timer(double limit) {
    super();
    this.limit = limit;
  }

  public double limit() {
    return limit;
  }
  public boolean isExceeded() {
    return value() >= limit;
  }
  public double ratio() {
    return 1 - (value() / limit);
  }

  public void reset(double limit) {
    super.reset();
    this.limit = limit;
  }
}
