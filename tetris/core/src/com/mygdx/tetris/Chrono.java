package com.mygdx.tetris;

import com.badlogic.gdx.utils.TimeUtils;

public class Chrono {
  private long start;

  public Chrono() {
    reset();
  }

  double value() {
    return (TimeUtils.nanoTime() - start) / Math.pow(10, 9);
  }

  public void reset() {
    start = TimeUtils.nanoTime();
  }
}
