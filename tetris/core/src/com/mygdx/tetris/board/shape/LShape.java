package com.mygdx.tetris.board.shape;

import com.mygdx.tetris.board.shape.block.Coords;
import com.mygdx.tetris.board.shape.block.Tile;

public class LShape extends Shape {
  public LShape() {
    super(
      new Coords(1, 0),
      new Coords(-1, 0),
      new Coords(-1, -1),
      Tile.L
    );
  }
}
