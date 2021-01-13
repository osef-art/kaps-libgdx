package com.gdx.kaps.level.caps;

import com.gdx.kaps.level.Sidekick;

import java.util.*;
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
    COLOR_12 (new java.awt.Color(235, 150, 130)),
    COLOR_13 (new java.awt.Color(70,  50,  130));

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