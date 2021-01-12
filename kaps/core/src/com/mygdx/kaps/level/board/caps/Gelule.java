package com.mygdx.kaps.level.board.caps;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.kaps.Kaps;
import com.mygdx.kaps.level.board.BoardObject;
import com.mygdx.kaps.utils.Timer;
import com.mygdx.kaps.utils.Vector;

import java.awt.*;
import java.util.List;

public class Gelule extends BoardObject {
  private final Timer timeBeforeFreeze;
  private final Caps center;
  private final Caps neighbor;

  public Gelule(Caps first, Caps second) {
    super(first.x(), first.y());
    center = new Caps(first);
    neighbor = new Caps(second);
    timeBeforeFreeze = new Timer(5, false);
    //timeBeforeFreeze = new Timer(0.75, false);
  }
  public Gelule(Gelule gelule, int x, int y) {
    this(
      new Caps(x, y, gelule.center.color(), gelule.center.look()),
      new Caps(x+1, y, gelule.neighbor.color(), gelule.neighbor.look())
    );
  }
  public Gelule(Gelule gelule, Look look) {
    this(gelule);
    center.setLook(look);
    neighbor.setLook(look.opposite());
  }
  public Gelule(Gelule gelule) {
    this(gelule.center, gelule.neighbor);
    acc().set(gelule.acc());
  }
  public Gelule(List<CapsColor> colors) {
    this(
      new Caps(0, 0, CapsColor.random(colors), Look.Left),
      new Caps(1, 0, CapsColor.random(colors), Look.Right)
    );
  }

  // getters
  public Caps center() {
    return center;
  }
  public Caps neighbor() {
    return neighbor;
  }
  public boolean isFrozen() {
    return super.isFrozen() || timeBeforeFreeze.isExceeded();
  }
  public Vector acc() {
    return center.acc();
  }
  public Rectangle hitbox() {
    return new Rectangle(
      Math.min(center.hitbox().x, neighbor.hitbox().x),
      Math.min(center.hitbox().y, neighbor.hitbox().y),
      (center.isLooking(Look.Up, Look.Down) ? 1 : 1.75f) * center.hitbox().width,
      (center.isLooking(Look.Up, Look.Down) ? 1.75f : 1) * center.hitbox().height
    );
  }
  public Rectangle zonebox() {
    boolean horizontal = center.isLooking(Look.Left, Look.Right);
    return new Rectangle(
      Math.min(center.zonebox().x, neighbor.zonebox().x),
      Math.min(center.zonebox().y, neighbor.zonebox().y),
      (horizontal ? 2 : 1) * center.zonebox().width,
      (horizontal ? 1 : 2) * center.zonebox().height
    );
  }
  public Rectangle supposedZone() {
    return new Rectangle(
      Math.min(center.supposedZone().x, neighbor.supposedZone().x),
      Math.min(center.supposedZone().y, neighbor.supposedZone().y),
      zonebox().width,
      zonebox().height
    );
  }

  // setters
  public void setColors(Gelule gelule) {
    center.setColor(gelule.center.color());
    neighbor.setColor(gelule.neighbor.color());
  }

  // collisions
  @Override
  public boolean collidesPile() {
    return center.collidesPile() && neighbor.collidesPile();
  }
  public boolean collidesPile(Look look) {
    if (center.isLooking(look)) return center.collidesPile();
    if (neighbor.isLooking(look)) return neighbor.collidesPile();
    return false;
  }
  public boolean collidesWall(Look look) {
    switch (look) {
      case Left:
        return center.collidesWall(Look.Left) || neighbor.collidesWall(Look.Left);
      case Up:
        return center.collidesWall(Look.Up) || neighbor.collidesWall(Look.Up);
      case Right:
        return center.collidesWall(Look.Right) || neighbor.collidesWall(Look.Right);
      case Down:
        return center.collidesWall(Look.Down) || neighbor.collidesWall(Look.Down);
    }
    return false;
  }
  public boolean cantMoveLeft() {
    return center.cantMoveLeft() || neighbor.cantMoveLeft();
  }
  public boolean cantMoveRight() {
    return center.cantMoveRight() || neighbor.cantMoveRight();
  }
  public boolean cantDip() {
    return center.cantDip() || neighbor.cantDip();
  }

  // physical collisions
  @Override
  public boolean exceedsLeftPile() {
    return center.exceedsLeftPile() || neighbor.exceedsLeftPile();
  }
  public boolean exceedsRightPile() {
    return center.exceedsRightPile() || neighbor.exceedsRightPile();
  }
  public boolean exceedsSideWalls() {
    return center.exceedsSideWalls() || neighbor.exceedsSideWalls();
  }
  public boolean sticksLeft() {
    return center.sticksLeft() || neighbor.sticksLeft();
  }
  public boolean sticksRight() {
    return center.sticksRight() || neighbor.sticksRight();
  }

  // board moves
  public boolean reposition() {    // returns false if a simple move couldn't reposition it
    Look look = center.look();

    if (collidesWall(look.opposite()) || collidesPile(look.opposite())) {
      shift(look);
      updateNeighbor();
      if (collidesPile(look) || collidesWall(look)) return false;
    }
    else if (collidesWall(look) || collidesPile(look)) {
      shift(look.opposite());
      updateNeighbor();
    }
    return !(collidesPile(look.opposite()) || collidesWall(look.opposite()));

    /*for (Look look : Look.directions()) {
      if (collidesWall(look) || collidesPile(look)) {
        shift(look.opposite());
        updateNeighbor();
        return !collidesPile(look.opposite());
      }
    }
    return true;*/
  }
  private void shift(Look look) {
    switch (look) {
      case Left:
        center.moveTo(-1, 0);
        break;
      case Right:
        center.moveTo(1, 0);
        break;
      case Up:
        center.moveTo(0, 1);
        break;
      case Down:
        center.moveTo(0, -1);
        break;
    }
    updateNeighbor();
  }
  public void flip() {
    center.flip();
    neighbor.flip();

    switch (center.look()) {
      case Left:
        center.lift();
        center.moveTo(-0.5f, 0.5f);
        break;
      case Up:
        center.lift();
        center.moveTo(0.5f, 0.5f);
        break;
      case Right:
        center.moveTo(0.5f, -0.5f);
        break;
      case Down:
        center.moveTo(-0.5f, -0.5f);
        break;
    }
    if (exceedsSideWalls()) blockX();
    updateNeighbor();
  }
  public void dip() {
    if (!cantDip()) freeze();
    center.dip();
    neighbor.dip();
  }

  // physical moves
  @Override
  public void accLeft() {
    center.accLeft();
  }
  public void accRight() {
    center.accRight();
  }
  public void accDown() {
    center.accDown();
  }

  @Override
  public void setXAcc(double acc) {
    if (acc < 0 && sticksLeft()) blockX();
    else if (acc > 0 && sticksRight()) blockX();
    else center.setXAcc(acc);
  }

  public void moveX() {
    if (exceedsSideWalls()) blockX();
    else if (exceedsLeftPile() || exceedsRightPile()) blockX();
    else if (Kaps.options.autoCenterIsEnabled() && acc().x() != 0 && Math.abs(acc().x()) < 0.25) blockX();
    else super.moveX();
  }
  public void moveY() {
    if (cantDip()) {
      if (zonebox().y - supposedZone().y > Kaps.dimensions.halfTile()) blockY();// freeze();
      else blockY();
    }
    else {
      super.moveY();
      if (timeBeforeFreeze.isActive()) timeBeforeFreeze.disable();
    }
  }
  public void followPos() {
    center.followPos();
  }
  public void blockX() {
    center.blockX();
    neighbor.blockX();
  }
  public void blockY() {
    if (timeBeforeFreeze.isInactive()) timeBeforeFreeze.reset();

    center.blockY();
    updateNeighbor();
  }

  // update
  private void updateNeighbor() {
    switch (center.look()) {
      case Left:
        neighbor.moveTo(center, 1, 0);
        break;
      case Up:
        neighbor.moveTo(center, 0, -1);
        break;
      case Right:
        neighbor.moveTo(center, -1, 0);
        break;
      case Down:
        neighbor.moveTo(center, 0, 1);
        break;
    }
  }
  public void update() {
    center.update();
    super.update();
    updateNeighbor();
  }

  // render
  @Override
  public String toString() {
    return center.toString() + ">" + neighbor;
  }

  public void render() {
    center.render();
    neighbor.render();
    Kaps.renderer.renderRectangle(zonebox(), new Color(0,0,0,50));
  }
  public void render(Rectangle rect) {
    center.render(rect.x, rect.y, rect.width/2, rect.height);
    neighbor.render(rect.x + rect.width/2, rect.y, rect.width/2, rect.height);
  }
}
