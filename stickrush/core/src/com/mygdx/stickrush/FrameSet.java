package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.Texture;

public class FrameSet {
  Texture[] tab;

  public FrameSet(String path, int length) {
    tab = new Texture[length];
    for (int i = 0; i < length; i++) {
      tab[i] = new Texture(path + i + ".png");
    }
  }

  public Texture get(int n) {
    return tab[n];
  }

  public void dispose() {
    for (Texture img : tab) img.dispose();
  }
}
