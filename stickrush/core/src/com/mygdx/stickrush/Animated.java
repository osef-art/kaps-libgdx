package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.Texture;

public abstract class Animated extends ImgDrawable {
  FrameSet frames;
  int frame = 0;

  public Animated(float x, float y, float width, float height, FrameSet frames) {
    super(x, y, width, height);
    this.frames = frames;
    img = currentFrame();
  }

  public Texture currentFrame() {
    return frame(frame);
  }

  public Texture frame(int n) {
    return frames.get(n);
  }

  public void nextFrame() {
    frame = (frame + 1) % 16;
  }

  @Override
  public void dispose() {
    super.dispose();
    frames.dispose();
  }
}
