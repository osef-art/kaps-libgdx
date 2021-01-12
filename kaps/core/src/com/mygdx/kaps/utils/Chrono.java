package com.mygdx.kaps.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class Chrono {
  private long start;

  public Chrono() {
    reset();
  }

  public double value() {
    return (TimeUtils.nanoTime() - start) / Math.pow(10, 9);
  }
  public void reset() {
    start = TimeUtils.nanoTime();
  }
}
