package com.mygdx.kaps.level.board;

import com.mygdx.kaps.level.board.caps.Caps;

import java.util.ArrayList;

public class Line extends ArrayList<Caps> {
  public Line(int width) {
    super();
    for (int i = 0; i < width; i++) add(null);
  }

  public void pop(int x) {
    get(x).pop();
  }
  public void clear(int x) {
    set(x, null);
  }
}
