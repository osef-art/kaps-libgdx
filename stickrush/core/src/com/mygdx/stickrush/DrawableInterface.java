package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.Texture;

public interface DrawableInterface {
  Texture currentFrame();
  void addTextures();
  void dispose();
  void render();
}
