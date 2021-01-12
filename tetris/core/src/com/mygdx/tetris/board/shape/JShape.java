package com.mygdx.tetris.board.shape;

import com.mygdx.tetris.board.shape.block.Coords;
import com.mygdx.tetris.board.shape.block.Tile;

public class JShape extends Shape {
  public JShape() {
    super(
      new Coords(1, 0),
      new Coords(0, -1),
      new Coords(1, -1),
      Tile.X
    );
  }
}
