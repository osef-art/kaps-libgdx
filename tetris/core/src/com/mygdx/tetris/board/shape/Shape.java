package com.mygdx.tetris.board.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.tetris.board.Board;
import com.mygdx.tetris.board.shape.block.Coords;
import com.mygdx.tetris.board.shape.block.Block;
import com.mygdx.tetris.board.shape.block.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Shape extends ArrayList<Block> {
  private final Texture img;

  Shape(Coords coords1, Coords coords2, Coords coords3, Tile type) {
    add(new Block(type));
    add(new Block(coords1, type));
    add(new Block(coords2, type));
    add(new Block(coords3, type));
    img = new Texture(Gdx.files.internal("shape_" + type.name() + ".png"));
  }
  private Shape() {
    super();
    img = null;
  }

  public static Shape randomShape() {
    int rand = new Random().nextInt(7);
    switch (rand) {
      case 0:
        return new XShape();
      case 1:
        return new JShape();
      case 2:
        return new LShape();
      case 3:
        return new SShape();
      case 4:
        return new ZShape();
      case 5:
        return new IShape();
      case 6:
        return new TShape();
    }
    return new IShape();
  }
  public Shape copy() {
    return shift(0, 0);
  }
  public Shape shift(int x, int y) {
    Shape copy = new Shape();
    for (Block block : this) copy.add(block.shift(x, y));
    return copy;
  }
  public Texture img() {
    return img;
  }

  // getters
  public Block center() {
    return get(0);
  }

  // setters
  public void draw() {
    draw(1);
  }
  public void draw(float alpha) {
    for (Block block : this) block.draw(alpha);
  }
  public void add(int x, int y) {
    for (Block block : this) block.add(x, y);
  }

  // move
  public void moveLeft() {
    add(-1, 0);
  }
  public void moveRight() {
    add(1, 0);
  }
  public void dip() {
    add(0, -1);
  }
  public void flip() {
    for (Block block : this) {
      if (block.equals(center())) continue;
           if (block.x() == center().x() && block.y() >  center().y()) block.add( 1, -1); // N
      else if (block.x() >  center().x() && block.y() >  center().y()) block.add( 0, -2); // NE
      else if (block.x() >  center().x() && block.y() == center().y()) block.add(-1, -1); // E
      else if (block.x() >  center().x() && block.y() <  center().y()) block.add(-2,  0); // SE
      else if (block.x() == center().x() && block.y() <  center().y()) block.add(-1,  1); // S
      else if (block.x() <  center().x() && block.y() <  center().y()) block.add( 0,  2); // SO
      else if (block.x() <  center().x() && block.y() == center().y()) block.add( 1,  1); // O
      else if (block.x() <  center().x() && block.y() >  center().y()) block.add( 2,  0); // NO
    }
  }

  public boolean collidesLeftWall() {
    for (Block block : this) if (block.x() < 0) return true;
    return false;
  }
  public boolean collidesBottom() {
    for (Block block : this) if (block.y() < 0) return true;
    return false;
  }
  public boolean collidesRightWall(Board board) {
    for (Block block : this) if (board.width() <= block.x()) return true;
    return false;
  }
  public boolean collidesTop(Board board) {
    for (Block block : this) if (board.height() <= block.y()) return true;
    return false;
  }
  public boolean collidesPile(Board board) {
    for (Block block : this) {
      if (block.x() >= board.width() || block.y() >= board.height()) continue;
      if (board.get(block.y()).get(block.x()) != Tile.Void) return true;
    }
    return false;
  }
}
