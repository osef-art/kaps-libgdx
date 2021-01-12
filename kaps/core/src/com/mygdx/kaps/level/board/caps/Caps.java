package com.mygdx.kaps.level.board.caps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.kaps.Kaps;
import com.mygdx.kaps.level.board.BoardObject;
import com.mygdx.kaps.utils.Timer;

public class Caps extends BoardObject {
  private CapsColor color;
  private final Timer fps;
  private boolean popping;
  private Sprite img;
  private Look look;
  private int frame;

  Caps(int x, int y, CapsColor color, Look look) {
    super(x, y);
    fps = new Timer(0.1);
    this.color = color;
    this.look = look;
    frame = 0;
    updateSprite();
  }
  public Caps(Caps caps) {
    this(caps.x(), caps.y(), caps.color, caps.look);
    setAcc(acc());
    zonebox().x = caps.zonebox().x;
    zonebox().y = caps.zonebox().y;

    if (caps.isFrozen()) freeze();
    else unfreeze();
  }

  // getters
  public Look look() {
    return look;
  }
  public Caps shifted(int x, int y) {
    return new Caps(x() + x, y() + y, color, look);
  }
  public boolean popped() {
    return frame == 7 && fps.isExceeded();
  }
  public boolean isSameColorThan(Caps caps) {
    return color == caps.color;
  }
  public boolean isLooking(Look... looks) {
    for (Look look : looks) if (this.look == look) return true;
    return false;
  }
  CapsColor color() {
    return color;
  }

  // setters
  void setColor(CapsColor color) {
    this.color = color;
    updateSprite();
  }
  void setLook(Look look) {
    this.look = look;
  }

  // collisions
  // moves
  public void pop() {
    fps.reset();
    popping = true;
    updateSprite();
  }
  public void dip() {
    if (cantDip()) freeze();
    else addY(-1);
  }
  public void lift() {
    addY(1);
  }
  public void unlink() {
    look = Look.None;
    updateSprite();
    freeze();
  }
  private void shiftLeft() {
    if (!cantMoveLeft()) addX(-1);
  }
  private void shiftRight() {
    if (!cantMoveRight()) addX(1);
  }
  void flip() {
    look = look.flipped();
    updateSprite();
  }

  // physical moves
  public void setXAcc(double acc) {
    if (acc < 0 && sticksLeft()) blockX();
    else if (acc > 0 && sticksRight()) blockX();
    else super.setXAcc(acc);
  }
  public void setYAcc(double acc) {
    if (acc > 0) super.setYAcc(acc);
  }

  public void moveX() {
    if (exceedsSideWalls()) blockX();
    else if (exceedsLeftPile() || exceedsRightPile()) blockX();
    else if (Kaps.options.autoCenterIsEnabled() && acc().x() != 0 && Math.abs(acc().x()) < 0.25) blockX();
    else super.moveX();
  }
  public void moveY() {
    if (cantDip()) blockY();
    else super.moveY();
  }
  void moveTo(Caps caps, float x, float y) {
    set(caps.x() + (int) x, caps.y() + (int) y);
    zonebox().x = caps.zonebox().x + (int) (x * caps.zonebox().width);
    zonebox().y = caps.zonebox().y - (int) (y * caps.zonebox().height);
  }
  void moveTo(float x, float y) {
    moveTo(this, x, y);
  }

  public void followPos() {
    if (zonebox().x < supposedZone().x - Kaps.dimensions.halfTile()) shiftLeft();
    if (supposedZone().x + supposedZone().width + Kaps.dimensions.halfTile() < zonebox().x + zonebox().width) shiftRight();
  }
  public void fall() {
    super.fall();
    if (supposedZone().y + Kaps.dimensions.tile.height < zonebox().y) dip();
  }

  // update
  private void updateSprite() {
    String path = popping ?
                  "img/caps/pop/" + color + '_' + frame + ".png" :
                  "img/caps/" + color + look + ".png";
    img = new Sprite(new Texture(path));
    img.flip(false, true);
  }
  public void updatePopAnim() {
    if (popping) {
      if (fps.isExceeded() && frame < 7) {
        frame++;
        fps.reset();
        updateSprite();
      }
    }
  }

  // render
  public void render() {
    render(zonebox());
  }
  public void render(Rectangle rectangle) {
    render(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }
  void render(float x, float y, float width, float height) {
    if (!Kaps.options.hitboxesAreEnabled()) {
      Kaps.renderer.renderRectangle(supposedZone(), color.rgba());
      //Kaps.renderer.renderRectangle(zonebox(), new Color(0, 0, 0, 50));
    }

    Kaps.draw(img, x, y, width, height);
    /*
    Kaps.renderer.drawText(
      "(" + x() + "," + y() + ")",
      Kaps.dimensions.boardMargin + x() * Kaps.dimensions.tile.width,
      Kaps.dimensions.boardMargin + (Kaps.level.board().topLineIndex() - y()) * Kaps.dimensions.tile.height
    );
    */
  }
}