package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public abstract class ImgDrawable implements DrawableInterface {
  final SpriteBatch batch = new SpriteBatch();
  final Rectangle zone;
  Texture img;

  private ImgDrawable(float x, float y, float w, float h, Texture t) {
    zone = new Rectangle(x, y, w, h);
    img = t;
  }
  public ImgDrawable(float x, float y, float w, float h) {
    this(x, y, w, h, null);
  }
  public ImgDrawable(Rectangle r, Texture t) {
    this(r.x, r.y, r.width, r.height, t);
  }

  public Texture currentFrame() {
    return img;
  }

  @Override
  public void render() {
    batch.setProjectionMatrix(Game.camera.combined);
    batch.begin();
    batch.draw(currentFrame(), zone.x, zone.y, zone.width, zone.height);
    batch.end();
  }

  @Override
  public void dispose() {
    batch.dispose();
    img.dispose();
  }
}
