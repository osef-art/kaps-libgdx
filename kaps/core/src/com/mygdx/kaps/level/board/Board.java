package com.mygdx.kaps.level.board;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.kaps.Kaps;
import com.mygdx.kaps.level.board.caps.Caps;
import com.mygdx.kaps.level.board.caps.Gelule;
import com.mygdx.kaps.level.board.caps.Look;
import com.mygdx.kaps.utils.Coords;

import java.awt.Color;
import java.util.ArrayList;

public class Board extends ArrayList<Line> {
  private final ArrayList<Caps> poppingCaps;
  private final ArrayList<Caps> fallingCaps;
  private final ArrayList<Gelule> gelules;
  private final int minimumCombo = 4;
  private final Coords size;
  private BoardObject selected;

  public Board(int width, int height) {
    super();
    selected = null;
    gelules = new ArrayList<>();
    fallingCaps = new ArrayList<>();
    poppingCaps = new ArrayList<>();
    size = new Coords(width, height);
    for (int i = 0; i < size.y(); i++) add(new Line(size.x()));
  }

  // getters
  public int width() {
    return size.x();
  }
  public int height() {
    return size.y();
  }
  public int xMiddle() {
    return width()/2 - 1;
  }
  public int topLineIndex() {
    return height() - 1;
  }
  public Caps get(int x, int y) {
    return get(y).get(x);
  }
  public Gelule gelule(int index) {
    return gelules.get(index);
  }
  public BoardObject selection() {
    return selected;
  }

  // checkers
  private boolean isEmpty(Caps caps) {
    return caps == null;
  }
  private boolean isEmpty(int x, int y) {
    return isEmpty(get(x, y));
  }
  private boolean isLinkedLeft(int x, int y) {
    return isCaps(x+1, y) && get(x, y).isLooking(Look.Left) && get(x+1, y).isLooking(Look.Right);
  }
  private boolean isLinkedRight(int x, int y) {
    return isCaps(x-1, y) && get(x, y).isLooking(Look.Right) && get(x-1, y).isLooking(Look.Left);
  }
  private boolean isLinkedUp(int x, int y) {
    return isCaps(x, y-1) && get(x, y).isLooking(Look.Up) && get(x, y-1).isLooking(Look.Down);
  }
  private boolean isLinkedDown(int x, int y) {
    return isCaps(x, y+1) && get(x, y).isLooking(Look.Down) && get(x, y+1).isLooking(Look.Up);
  }
  private boolean isOutOfBoard(int x, int y) {
    return x < 0 || width() <= x || y < 0 || height() <= y;
  }
  public boolean isCaps(int x, int y) {
    return !isOutOfBoard(x, y) && !isEmpty(x, y);
  }
  public boolean hasNoGelule() {
    return gelules.size() == 0;
  }
  public boolean geluleIsSelected() {
    return selected == gelule(0);
  }
  public boolean hasSomethingSelected() {
    return selected != null;
  }

  // gelule
  public void loadNewGelule(Gelule gelule) {
    gelules.add(gelule);
    if (gelule.collidesPile()) {
      System.out.println("GAME OVER !");
      System.exit(0);
    }
  }
  public void selectObject(float x, float y) {
    for (Gelule gelule : gelules) {
      if (gelule.checkIfSelected(x, y)) {
        selected = gelule;
        return;
      }
    }
    for (Caps caps : fallingCaps) {
      if (caps.checkIfSelected(x, y)) {
        selected = caps;
        return;
      }
    }
  }
  public void deselectGelule() {
    selected = null;
  }
  public void swapColors(Gelule gelule1, Gelule gelule2) {
    Gelule tmp = new Gelule(gelule1);
    gelule1.setColors(gelule2);
    gelule2.setColors(tmp);
  }
  public void removeGelule() {
    gelules.clear();
  }

  // moves
  public void moveLeft() {
    gelule(0).accLeft();
  }
  public void moveRight() {
    gelule(0).accRight();
  }
  public void moveDown() {
    gelule(0).accDown();
  }
  public void flipGelule() {
    Gelule flipped = new Gelule(gelule(0));
    flipped.flip();
    if (flipped.reposition()) gelules.set(0, flipped);
  }
  public void dropGelule() {
    while (gelule(0).canDip()) gelule(0).dip();
    gelule(0).freeze();
  }

  // modifications
  private void set(Caps caps) {
    caps.freeze();
    get(caps.y()).set(caps.x(), new Caps(caps));
  }
  private void pop(int x, int y) {
    if (isEmpty(x, y)) return;
    Caps caps = get(x, y);

    switch (caps.look()) {
      case Left:
        get(x+1, y).unlink();
        break;
      case Right:
        get(x-1, y).unlink();
        break;
      case Up:
        get(x, y-1).unlink();
        break;
      case Down:
        get(x, y+1).unlink();
        break;
    }
    get(y).pop(x);
    poppingCaps.add(caps);
    clear(x, y);
  }
  private void dip(int x, int y) {
    Caps caps = get(x, y);
    caps.dip();
    set(caps);
    clear(x, y);
  }
  private void clear(int x, int y) {
    get(y).clear(x);
  }
  private void startFalling(Caps caps) {
    caps.unfreeze();
    fallingCaps.add(new Caps(caps));
    clear(caps.x(), caps.y());
  }
  private void startFalling(int x, int y) {
    if (isLinkedLeft(x, y) && get(x+1, y).canDip()) {
      dip(x, y);
      dip(x+1, y);
    }
    else if (isLinkedRight(x, y) && get(x-1, y).canDip()) {
      dip(x, y);
      dip(x-1, y);
    }
    else if (isLinkedUp(x, y)) {
      dip(x, y-1);
      dip(x, y);
    }
    else if (isLinkedDown(x, y)) {
      dip(x, y);
      dip(x, y+1);
    }
    else if (get(x, y).isLooking(Look.None)) {
      if (isCaps(x, y+1) && !get(x, y+1).isLooking(Look.None)) {
        dip(x, y);
        return;
      }
      startFalling(get(x, y));
    }
  }

  // add
  private void addGelule(Gelule gelule) {
    set(gelule.center());
    set(gelule.neighbor());

    checkForLines();
    checkForColumns();
  }
  private void addCaps(Caps caps) {
    while (isCaps(caps.x(), caps.y())) caps.lift();
    set(caps);

    checkForLines();
    checkForColumns();
  }

  // update
  private void checkForLines() {
    for (int y = 0; y < height(); y++) {
      ArrayList<Caps> toBeDeleted = new ArrayList<>();

      for (int x = 0; x < width(); x++) {
        Caps caps = get(x, y);
        // the first directly starts a chain
        if (x == 0) {
          if (!isEmpty(caps)) toBeDeleted.add(caps);
          continue;
        }

        Caps prev = get(x-1, y);
        // if prev is empty, we start a chain as well
        if (isEmpty(prev)) {
          if (!isEmpty(caps)) toBeDeleted.add(caps);
          continue;
        }

        // if end is reached and ends combo, we add it to the combo
        if (x == width()-1 && isCaps(x, y) && get(x, y).isSameColorThan(toBeDeleted.get(0))) toBeDeleted.add(caps);

        // if current is empty / if previous has not the same color / end reached
        if (isEmpty(caps) || !caps.isSameColorThan(prev) || x == width()-1) {
          // if combo, pop caps
          if (toBeDeleted.size() >= minimumCombo) {
            for (Caps tile : toBeDeleted) pop(tile.x(), tile.y());
          }
          // clear the candidates
          toBeDeleted.clear();
        }

        // add caps to the chain for the next check
        if (!isEmpty(caps)) toBeDeleted.add(caps);
      }
    }
  }
  private void checkForColumns() {
    for (int x = 0; x < width(); x++) {
      ArrayList<Caps> toBeDeleted = new ArrayList<>();

      for (int y = 0; y < height(); y++) {
        Caps caps = get(x, y);
        // the first directly starts a chain
        if (y == 0) {
          if (!isEmpty(caps)) toBeDeleted.add(caps);
          continue;
        }

        Caps prev = get(x, y-1);
        // if prev is empty, we start a chain as well
        if (isEmpty(prev)) {
          if (!isEmpty(caps)) toBeDeleted.add(caps);
          continue;
        }

        // if end is reached and ends combo, we add it to the combo
        if (y == topLineIndex() && isCaps(x, y) && get(x, y).isSameColorThan(toBeDeleted.get(0))) toBeDeleted.add(caps);

        // if current is empty / if previous has not the same color / end reached
        if (isEmpty(caps) || !caps.isSameColorThan(prev) || y == topLineIndex()) {
          // if combo, pop caps
          if (toBeDeleted.size() >= minimumCombo) {
            for (Caps tile : toBeDeleted) pop(tile.x(), tile.y());
          }
          // clear the candidates
          toBeDeleted.clear();
        }

        // add caps to the chain for the next check
        if (!isEmpty(caps)) toBeDeleted.add(caps);
      }
    }
  }

  // update
  public void updateFallingCaps() {
    for (Caps caps : fallingCaps) {
      caps.update();

      if (caps.isFrozen()) {
        addCaps(caps);
        fallingCaps.remove(caps);
        return;
      }
    }
  }
  public void updatePoppingCaps() {
    for (Caps caps : poppingCaps) {
      caps.updatePopAnim();

      if (caps.popped()) {
        poppingCaps.remove(caps);
        return;
      }
    }
  }
  public void updateGelules() {
    for (Gelule gelule : gelules) {
      gelule.update();

      if (gelule.isFrozen()) {
        addGelule(gelule);
        gelules.remove(gelule);
        return;
      }
    }
  }
  public void updateCaps() {
    for (int x = 0; x < width(); x++) {
      for (int y = 1; y < height(); y++) {
        if (isCaps(x, y) && get(x, y).canDip()) startFalling(x, y);
      }
    }
  }
  public void update() {
    updateGelules();
    updateCaps();
    updatePoppingCaps();
    updateFallingCaps();
  }

  // render
  public void render() {
    for (int y = 0; y < height(); y++) {
      Line line = get(y);
      for (int x = 0; x < line.size(); x++) {
        Rectangle zone = new Rectangle(
          Kaps.dimensions.boardMargin + x * Kaps.dimensions.tile.width,
          Kaps.dimensions.boardMargin + (topLineIndex() - y) * Kaps.dimensions.tile.height,
          Kaps.dimensions.tile.width,
          Kaps.dimensions.tile.height
        );
        Kaps.renderer.renderRectangle(
          zone,
          x%2 == y%2 ? new Color(100, 110, 130) : new Color(90, 100, 120)
        );
        if (Kaps.options.previewIsEnabled() && !hasNoGelule()) {
          if (gelule(0).x() == x || gelule(0).neighbor().x() == x) Kaps.renderer.renderRectangle(zone, new Color(210, 230, 250, 50));
        }
        if (line.get(x) != null) line.get(x).render(zone);
      }
    }
    for (Caps caps : poppingCaps) caps.render();
    for (Caps caps : fallingCaps) caps.render();
    for (Gelule gelule : gelules) gelule.render();
  }
}