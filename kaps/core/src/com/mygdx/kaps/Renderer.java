package com.mygdx.kaps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Renderer extends ShapeRenderer {
  public static FreeTypeFontParameter parameter;
  private final BitmapFont font;

  public Renderer() {
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Gotham.ttf"));
    parameter = new FreeTypeFontParameter();
    parameter.size = 25;
    parameter.flip = true;
    parameter.color = new Color(1, 1, 1, 0.4f);
    font = generator.generateFont(parameter);
    generator.dispose();
  }

  private static Color toGDXColor(java.awt.Color color) {
    return new Color(
      (float) (color.getRed() / 255.),
      (float) (color.getGreen() / 255.),
      (float) (color.getBlue() / 255.),
      (float) (color.getAlpha() / 255.)
    );
  }

  public void renderRectangle(float x, float y, float width, float height, java.awt.Color color) {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    Kaps.renderer.begin(ShapeType.Filled);
    Kaps.renderer.setColor(toGDXColor(color));
    Kaps.renderer.rect(x, y, width, height);
    Kaps.renderer.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }
  public void renderRectangle(Rectangle rect, java.awt.Color color) {
    renderRectangle(rect.x, rect.y, rect.width, rect.height, color);
  }

  public void drawText(float value, float x, float y) {
    drawText(value + "", x, y);
  }
  public void drawText(String text, float x, float y) {
    Kaps.batch.begin();
    font.draw(Kaps.batch, text, x, y);
    Kaps.batch.end();
  }
}
