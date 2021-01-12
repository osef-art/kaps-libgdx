package com.mygdx.tetris.board;

import com.mygdx.tetris.board.shape.block.Tile;

import java.util.ArrayList;

public class Line extends ArrayList<Tile> {
  public Line(int length) {
    for (int i = 0; i < length; i++) add(Tile.Void);
  }

  public void draw(int y) {
    for (int x = 0; x < size(); x++) get(x).draw(x, y, 1);
  }

  public boolean isFilled() {
    for (Tile tile : this) if (tile == Tile.Void) return false;
    return true;
  }
}
