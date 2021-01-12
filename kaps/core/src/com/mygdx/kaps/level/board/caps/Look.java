package com.mygdx.kaps.level.board.caps;

import java.util.ArrayList;
import java.util.Arrays;

public enum Look {
  Left  ("left"),
  Up    ("up"),
  Down  ("down"),
  Right ("right"),
  None
  ;

  private final String name;

  Look(String name) {
    this.name = "_" + name;
  }
  Look() {
    this.name = "";
  }

  public static ArrayList<Look> directions() {
    return new ArrayList<>(Arrays.asList(Left, Right, Up, Down));
  }

  public Look flipped() {
    switch (this) {
      case Left:
        return Up;
      case Up:
        return Right;
      case Right:
        return Down;
      case Down:
        return Left;
    }
    return None;
  }
  public Look opposite() {
    switch (this) {
      case Left:
        return Right;
      case Up:
        return Down;
      case Right:
        return Left;
      case Down:
        return Up;
    }
    return None;
  }

  @Override
  public String toString() {
    return name;
  }
}
