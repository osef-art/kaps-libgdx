package com.mygdx.kaps.level.board;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.kaps.Kaps;
import com.mygdx.kaps.level.board.caps.Look;
import com.mygdx.kaps.utils.Coords;
import com.mygdx.kaps.utils.Vector;

public class BoardObject extends Coords {
  private final Rectangle zonebox;
  private final Vector acceleration;
  private final double moveSpeed;
  private final double friction;
  private boolean frozen;

  public BoardObject(int x, int y) {
    super(x, y);
    moveSpeed = 0.75;
    friction = 0.9;
    acceleration = new Vector();
    zonebox = new Rectangle(
      Kaps.dimensions.boardMargin + x() * Kaps.dimensions.tile.width,
      Kaps.dimensions.boardMargin,
      Kaps.dimensions.tile.width,
      Kaps.dimensions.tile.height
    );
  }

  // getters
  public Vector acc() {
    return acceleration;
  }
  public Rectangle zonebox() {
    return zonebox;
  }
  public Rectangle hitbox() {
    return new Rectangle(
      zonebox.x - zonebox.width/4,
      zonebox.y - zonebox.height/4,
      zonebox.width * 3/2,
      zonebox.height * 3/2
    );
  }
  public Rectangle supposedZone() {
    return new Rectangle(
      Kaps.dimensions.boardMargin + x() * Kaps.dimensions.tile.width,
      Kaps.dimensions.boardMargin + (Kaps.level.board().height()-1 - y()) * Kaps.dimensions.tile.height,
      zonebox.width,
      zonebox.height
    );
  }
  public BoardObject shifted(int x, int y) {
    return new BoardObject(x() + x, y() + y);
  }
  public boolean isFrozen() {
    return frozen;
  }

  // setters
  public void setAcc(Vector acc) {
    acceleration.set(acc);
  }
  public void setXAcc(double acc) {
    acceleration.setX(acc);
  }
  public void setYAcc(double acc) {
    acceleration.setY(acc);
  }

  // collisions
  public boolean collidesWall(Look look) {
    boolean collidesTop = Kaps.level.board().height() <= y();
    boolean collidesLeft = x() < 0;
    boolean collidesRight = Kaps.level.board().width() <= x();
    boolean collidesBottom = y() < 0;

    switch (look) {
      case Left:
        return collidesLeft;
      case Right:
        return collidesRight;
      case Up:
        return collidesTop;
      case Down:
        return collidesBottom;
    }
    return collidesTop || collidesBottom || collidesLeft || collidesRight;
  }
  public boolean collidesPile() {
    return Kaps.level.board().isCaps(x(), y());
  }
  public boolean cantMoveLeft() {
    return shifted(-1, 0).collidesPile() || shifted(-1, 0).collidesWall(Look.Left);
  }
  public boolean cantMoveRight() {
    return shifted(1, 0).collidesPile() || shifted(1, 0).collidesWall(Look.Right);
  }
  public boolean cantDip() {
    return shifted(0, -1).collidesPile() || shifted(0, -1).collidesWall(Look.Down);
  }
  public boolean canDip() {
    return !cantDip();
  }

  // physical collisions
  public boolean exceedsSideWalls() {
    return zonebox.x < Kaps.dimensions.boardMargin ||
             Kaps.dimensions.boardMargin + Kaps.dimensions.board.width < zonebox.x + zonebox.width;
  }
  public boolean exceedsLeftPile() {
    return cantMoveLeft() && Kaps.level.board().isCaps(x()-1, y()) && Kaps.level.board().get(x()-1, y()).zonebox().overlaps(zonebox);
  }
  public boolean exceedsRightPile() {
    return cantMoveRight() && Kaps.level.board().isCaps(x()+1, y()) && Kaps.level.board().get(x()+1, y()).zonebox().overlaps(zonebox);
  }
  public boolean sticksLeft() {
    return cantMoveLeft() && zonebox.x <= supposedZone().x;
  }
  public boolean sticksRight() {
    return cantMoveRight() && zonebox.x >= supposedZone().x;
  }


  // physical moves
  public void accLeft() {
    setXAcc(acceleration.x() - moveSpeed);
  }
  public void accRight() {
    setXAcc(acceleration.x() + moveSpeed);
  }
  public void accDown() {
    setYAcc(acceleration.y() + moveSpeed);
  }
  public void applyFriction() {
    acceleration.mult(friction, friction);
  }

  public void moveX() {
    if (exceedsSideWalls()) blockX();
    else if (exceedsLeftPile() || exceedsRightPile()) blockX();
    else zonebox.x += acceleration.x();
  }
  public void moveY() {
    zonebox.y += acceleration.y();
  }
  public void fall() {
    zonebox.y += 0.5;
  }
  public void followPos() {
  }

  // block
  public void blockX() {
    acceleration.setX(0);
    zonebox.x = supposedZone().x;
  }
  public void blockY() {
    acceleration.setY(0);
    zonebox.y = supposedZone().y;
  }
  public void freeze() {
    blockX();
    blockY();
    frozen = true;
  }
  public void unfreeze() {
    frozen = false;
  }

  // mouse control
  public boolean checkIfSelected(float x, float y) {
    return hitbox().contains(x, y);
  }
  public void followMouse(float x, float y) {
    setXAcc((x - (zonebox.x + zonebox.width/2)) /6);
    setYAcc((y - (zonebox.y + zonebox.height/2)) /4);
  }

  // update
  public void update() {
    moveX();
    moveY();
    applyFriction();
    fall();
    followPos();
  }
}
