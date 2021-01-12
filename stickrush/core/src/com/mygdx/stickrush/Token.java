package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.Texture;

import java.util.Arrays;
import java.util.Random;
import java.util.List;

public enum Token {
  PUNCH("punch"),
  KICK("kick"),
  PARRY("parry"),
  DODGE("dodge"),
  SPECIAL("special"),
  ;

  private static final List<Token> VALUES = Arrays.asList(values());
  private static final int SIZE = VALUES.size();
  private final Texture sprite;

  Token(String filename) {
    sprite = new Texture("tokens/" + filename + ".png");
  }

  public static Token random() {
    return VALUES.get(new Random().nextInt(SIZE));
  }

  public Texture texture() {
    return sprite;
  }
}
