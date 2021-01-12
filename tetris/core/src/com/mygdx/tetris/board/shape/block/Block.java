package com.mygdx.tetris.board.shape.block;

public class Block extends Coords {
  private final Tile type;

  public Block(int x, int y, Tile tile) {
    super(x, y);
    type = tile;
  }
  public Block(Coords coords, Tile tile) {
    this(coords.x(), coords.y(), tile);
  }
  public Block(Tile tile) {
    this(0, 0, tile);
  }

  public Tile type() {
    return type;
  }

  public Block shift(int x, int y) {
    return new Block(x() + x, y() + y, type);
  }

  public void draw(float alpha) {
    type.draw(x(), y(), alpha);
  }
}
