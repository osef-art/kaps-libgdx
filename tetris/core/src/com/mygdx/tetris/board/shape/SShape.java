package com.mygdx.tetris.board.shape;

import com.mygdx.tetris.board.shape.block.Coords;
import com.mygdx.tetris.board.shape.block.Tile;

public class SShape extends Shape {
  public SShape() {
    super(
      new Coords(1, 0),
      new Coords(0, -1),
      new Coords(-1, -1),
      Tile.S
    );
  }
}
