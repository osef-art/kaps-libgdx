package com.gdx.kaps.level.grid;

import com.gdx.kaps.Utils;
import com.gdx.kaps.level.sidekick.SidekickRecord;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Color {
    COLOR_1  (new java.awt.Color(110, 80,  235)),
    COLOR_2  (new java.awt.Color(90,  190, 235)),
    COLOR_3  (new java.awt.Color(220, 60,  40)),
    COLOR_4  (new java.awt.Color(180, 235, 60)),
    COLOR_5  (new java.awt.Color(50,  235, 215)),
    COLOR_6  (new java.awt.Color(215, 50,  100)),
    COLOR_7  (new java.awt.Color(220, 235, 160)),
    COLOR_8  (new java.awt.Color(40,  50,  60)),
    COLOR_9  (new java.awt.Color(180, 200, 220)),
    COLOR_10 (new java.awt.Color(100, 110, 170)),
    COLOR_11 (new java.awt.Color(50,  180, 180)),
    COLOR_12 (new java.awt.Color(235, 150, 140)),
    COLOR_13 (new java.awt.Color(70,  50,  130));

    private final com.badlogic.gdx.graphics.Color gdxColor;

    Color(java.awt.Color color) {
        gdxColor = new com.badlogic.gdx.graphics.Color(
          (float) (color.getRed() / 255.),
          (float) (color.getGreen() / 255.),
          (float) (color.getBlue() / 255.),
          (float) (color.getAlpha() / 255.)
        );
    }

    public static Color randomBlank() {
        var blankColors = Arrays.stream(values())
                            .filter(color -> Arrays.stream(SidekickRecord.values())
                                             .map(SidekickRecord::color)
                                            .noneMatch(color::equals))
                            .collect(Collectors.toList());

        return Utils.getRandomFrom(blankColors)
                 .orElseThrow(() -> new IllegalStateException("Create a color not associated with a sidekick."));
    }

    public static Color random(Set<Color> colors) {
        return Utils.getRandomFrom(colors).orElseThrow(
          () -> new IllegalArgumentException("Can't get a type from empty set.")
        );
    }

    public int id() {
        return ordinal() + 1;
    }

    public com.badlogic.gdx.graphics.Color value() {
        return gdxColor;
    }

    public com.badlogic.gdx.graphics.Color value(float alpha) {
        return new com.badlogic.gdx.graphics.Color(
          gdxColor.r, gdxColor.g, gdxColor.b, alpha
        );
    }
}