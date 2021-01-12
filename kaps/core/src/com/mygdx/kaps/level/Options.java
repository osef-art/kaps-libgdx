package com.mygdx.kaps.level;

public class Options {
  private boolean showHitboxes;
  private boolean showPreview;
  private boolean autoCenter;
  private double flipSpeed;

  public Options() {
    showHitboxes = false;
    showPreview = false;
    autoCenter = false;
    flipSpeed = 0.2;
  }

  public boolean hitboxesAreEnabled() {
    return showHitboxes;
  }
  public boolean previewIsEnabled() {
    return showPreview;
  }
  public boolean autoCenterIsEnabled() {
    return autoCenter;
  }
  public double flipSpeed() {
    return flipSpeed;
  }

  public void toggleAutoCenter() {
    autoCenter = !autoCenter;
  }
  public void togglePreview() {
    showPreview = !showPreview;
  }
  public void toggleHitboxes() {
    showHitboxes = !showHitboxes;
  }
}
