package com.gdx.kaps.level.caps;

import com.gdx.kaps.level.Sidekick;

import java.util.*;
import java.util.stream.Collectors;

public enum Color {
    TYPE_1  (new java.awt.Color(110, 80,  235)),
    TYPE_2  (new java.awt.Color(90,  190, 235)),
    TYPE_3  (new java.awt.Color(220, 60,  40)),
    TYPE_4  (new java.awt.Color(180, 235, 60)),
    TYPE_5  (new java.awt.Color(50,  235, 215)),
    TYPE_6  (new java.awt.Color(215, 50,  100)),
    TYPE_7  (new java.awt.Color(220, 235, 160)),
    TYPE_8  (new java.awt.Color(40,  50,  60)),
    TYPE_9  (new java.awt.Color(180, 200, 220)),
    TYPE_10 (new java.awt.Color(100, 110, 170)),
    TYPE_11 (new java.awt.Color(50,  180, 180)),
    TYPE_12 (new java.awt.Color(235, 150, 130)),
    TYPE_13 (new java.awt.Color(70,  50,  130));

    private final java.awt.Color color;

    Color(java.awt.Color color) {
        this.color = color;
    }

    public static Color randomBlank() {
        var sidekickColors = Arrays.stream(Sidekick.values())
                               .map(Sidekick::color)
                               .collect(Collectors.toList());

        var blankColors = Arrays.stream(values())
                            .filter(c -> !sidekickColors.contains(c))
                            .collect(Collectors.toList());

        if (blankColors.isEmpty()) {
            throw new NoSuchElementException("Need new color w/o sidekick.");
        }
        return new ArrayList<>(blankColors).get(new Random().nextInt(blankColors.size()));
    }

    public static Color random(Set<Color> colors) {
        if (colors.isEmpty()) {
            throw new IllegalArgumentException("Can't get a type from empty set.");
        }
        return new ArrayList<>(colors).get(new Random().nextInt(colors.size()));
    }

    public int id() {
        return ordinal() + 1;
    }
}