package com.mygdx.tetris.board.shape.block;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.tetris.Tetris;

import java.awt.*;

import static com.mygdx.tetris.Tetris.shapeRenderer;

public enum Tile {
  Void       (new Color(50, 60, 70)),
  X     ("X", new Color(100, 75, 200)),
  T     ("T", new Color(150, 125, 200)),
  Z     ("Z", new Color(125, 175, 250)),
  S     ("S", new Color(100, 150, 225)),
  L     ("L", new Color(75, 175, 175)),
  J     ("J", new Color(100, 200, 175)),
  I     ("I", new Color(150, 200, 100)),
  ;

  private final String name;
  private final Color color;
  private static final Coords dimensions = new Coords(Tetris.dimensions().x()/20, Tetris.dimensions().x()/20);

  Tile(String name, Color color) {
    this.color = color;
    this.name = name;
  }
  Tile(Color color) {
    this("", color);
  }

  public static Coords dimensions() {
    return dimensions;
  }

  @Override
  public String toString() {
    return name;
  }

  com.badlogic.gdx.graphics.Color toGDXColor(float alpha) {
    return new com.badlogic.gdx.graphics.Color(
      (float) (color.getRed() / 255.),
      (float) (color.getGreen() / 255.),
      (float) (color.getBlue() / 255.),
      (float) (color.getAlpha()*alpha / 255.)
    );
  }

  public void draw(int x, int y, float alpha) {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    if (this == Void && x %2 == y %2) alpha *= 0.9;
    shapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer().setColor(toGDXColor(alpha));
    shapeRenderer().rect(
      50 + x * dimensions.x(),
      (Tetris.dimensions().y() - (50 + 17*dimensions.y())) + y * dimensions.y(),
      dimensions.x(),
      dimensions.y()
    );
    shapeRenderer().end();
    if (this == Void) return;
    shapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer().setColor(1f, 1f, 1f, 0.05f);
    shapeRenderer().rect(
      50 + x * dimensions.x(),
      (Tetris.dimensions().y() - (50 + 17*dimensions.y())) + (y + 0.25f) * dimensions.y(),
      dimensions.x() * 0.75f,
      dimensions.y() * 0.75f
    );
    shapeRenderer().end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }
}
