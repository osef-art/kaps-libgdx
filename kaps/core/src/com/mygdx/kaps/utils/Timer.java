package com.mygdx.kaps.utils;

public class Timer extends Chrono {
  private boolean on;
  private final double limit;

  public Timer(double limit) {
    this(limit, true);
  }
  public Timer(double limit, boolean on) {
    super();
    this.on = on;
    this.limit = limit;
  }

  public boolean isActive() {
    return on;
  }
  public boolean isInactive() {
    return !on;
  }
  public boolean isExceeded() {
    return on && value() >= limit;
  }
  public double ratio() {
    return 1 - (value() / limit);
  }

  @Override
  public void reset() {
    super.reset();
    on = true;
  }
  public void disable() {
    on = false;
  }
}
