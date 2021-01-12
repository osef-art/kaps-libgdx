package com.mygdx.tetris.board.shape;

import com.mygdx.tetris.board.shape.block.Coords;
import com.mygdx.tetris.board.shape.block.Tile;

public class XShape extends Shape {
  public XShape() {
    super(
      new Coords(1, 0),
      new Coords(-1, 0),
      new Coords(1, -1),
      Tile.J
    );
  }
}
