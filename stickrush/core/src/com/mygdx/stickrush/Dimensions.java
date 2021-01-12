package com.mygdx.stickrush;

import com.badlogic.gdx.math.Rectangle;

public class Dimensions {
  final Rectangle window, info, scene, hero, grid, summons, tile, token, sceneFloor;
  final float tokenPadding, left, heroSize;

  public Dimensions(int windowW, int windowH) {
    window = new Rectangle(0, 0, windowW, windowH);
    left = window.height - window.width;
    summons = new Rectangle(0, 0, window.width, left / 4);
    grid = new Rectangle(0, summons.height, window.width, window.width);
    scene = new Rectangle(0, summons.height + grid.height, window.width, left*5 / 8);
    sceneFloor = new Rectangle(0, summons.height + grid.height, window.width, left / 8);
    heroSize = scene.height*3 / 4;
    hero = new Rectangle((window.width - heroSize) / 2, sceneFloor.y + sceneFloor.height*3 / 4, heroSize, heroSize);
    info = new Rectangle(0, summons.height + grid.height + scene.height, window.width, left / 8);
    tile = new Rectangle(0, 0, grid.width / 10, grid.width / 10);
    tokenPadding = tile.width / 10;
    token = new Rectangle(0, 0, tile.width - 2*tokenPadding, tile.width - 2*tokenPadding);
  }

  public Rectangle tile(int x, int y) {
    return tile(x, y, 0);
  }

  public Rectangle tile(int x, int y, float padding) {
    return new Rectangle(
      grid.x + padding + x * tile.width,
      grid.y + padding + y * tile.height,
      tile.width - 2*padding,
      tile.height - 2*padding
    );
  }

  public Rectangle tile(Position pos, float padding) {
    return new Rectangle(
      grid.x + padding + pos.getX() * tile.width,
      grid.y + padding + pos.getY() * tile.height,
      tile.width - 2*padding,
      tile.height - 2*padding
    );
  }
}
