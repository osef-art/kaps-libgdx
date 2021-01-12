package com.mygdx.tetris.board.shape;

import com.mygdx.tetris.board.shape.block.Coords;
import com.mygdx.tetris.board.shape.block.Tile;

public class TShape extends Shape {
  public TShape() {
    super(
      new Coords(-1, 0),
      new Coords(1, 0),
      new Coords(0, -1),
      Tile.T
    );
  }
}
