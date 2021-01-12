package com.mygdx.stickrush;

import com.badlogic.gdx.math.Rectangle;

public class Hero extends Animated implements DrawableInterface {
  public Hero(FrameSet frames, Rectangle r) {
    super(r.x, r.y, r.width, r.height, frames);
  }

  @Override
  public void addTextures() {

  }
}
