package com.mygdx.kaps.level.board.caps;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum CapsColor {
  Sean(      1, new Color(110, 80,  235)),
  Zyrame(    2, new Color(90,  190, 235)),
  Red(       3, new Color(220, 60,  40)),
  Mimaps(    4, new Color(180, 235, 60)),
  Painter(   5, new Color(50,  235, 215)),
  Xereth(    6, new Color(215, 50,  100)),
  Rive(      7, new Color(220, 235, 160)),
  Neutral1(  8, new Color(40,  50,  60)),
  Neutral2(  9, new Color(180, 200, 220)),
  Jim(      10, new Color(100, 110, 170)),
  Harmon(   11, new Color(50,  180, 180)),
  Neutral3( 13, new Color(70,  50,  130)),
  ;

  private final String name;
  private final Color color;

  CapsColor(int serial, Color color) {
    this.color = color;
    name = serial + "";
  }

  public Color rgba() {
    return color;
  }

  @Override
  public String toString() {
    return name;
  }

  public static CapsColor random(List<CapsColor> colors)  {
    return colors.get(new Random().nextInt(colors.size()));
  }

  public static ArrayList<CapsColor> randomSet(int size) {
    List<CapsColor> values = new ArrayList<>(Arrays.asList(CapsColor.values()));
    ArrayList<CapsColor> set = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      CapsColor color = random(values);
      values.remove(color);
      set.add(color);
    }
    return set;
  }
}
