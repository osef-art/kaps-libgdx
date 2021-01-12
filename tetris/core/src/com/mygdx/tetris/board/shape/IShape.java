package com.mygdx.tetris.board.shape;

import com.mygdx.tetris.board.shape.block.Block;
import com.mygdx.tetris.board.shape.block.Coords;
import com.mygdx.tetris.board.shape.block.Tile;

public class IShape extends Shape {
  public IShape() {
    super(
      new Coords(-1, 0),
      new Coords(1, 0),
      new Coords(2, 0),
      Tile.I
    );
  }

  @Override
  public void flip() {
    for (Block block : this) {
      if (block.equals(center())) continue;
           if (block.x() == center().x()     && block.y() == center().y() + 1) block.add(1, -1); // N
      else if (block.x() == center().x() + 1 && block.y() == center().y())     block.add(-1, -1); // E
      else if (block.x() == center().x()     && block.y() == center().y() - 1) block.add(-1, 1); // S
      else if (block.x() == center().x() - 1 && block.y() == center().y())     block.add(1, 1); // O
      else if (block.x() == center().x()     && block.y() == center().y() + 2) block.add(2, -2); // N-loin
      else if (block.x() == center().x() + 2 && block.y() == center().y())     block.add(-2, -2); // E-loin
      else if (block.x() == center().x()     && block.y() == center().y() - 2) block.add(-2, 2); // S-loin
      else if (block.x() == center().x() - 2 && block.y() == center().y())     block.add(2, 2); // O-loin
    }
  }
}
