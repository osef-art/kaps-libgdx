package com.gdx.kaps.level;

import com.gdx.kaps.level.caps.Color;

import java.util.Set;

public enum Sidekick {
    // TODO: add powers
    SEAN        (Color.TYPE_1), // INFO: Hits a tile then its neighbors
    ZYRAME      (Color.TYPE_2), // INFO: Hits 2 random germs
    RED         (Color.TYPE_3), // INFO: Slices a.random column
    MIMAPS      (Color.TYPE_4), // INFO: Hits 3 random.tiles
    PAINT       (Color.TYPE_5), // INFO: Paints 5 tiles in mate's color
    XERETH      (Color.TYPE_6), // INFO: Slices two.diagonals
    SIDEKICK_7  (Color.TYPE_7), // INFO:
    JIM         (Color.TYPE_10), // INFO: Slices a.random line
    COLOR       (Color.TYPE_11), // INFO: Generates a.single-colored gelule
    SIDEKICK_12 (Color.TYPE_12), // INFO:
    SIDEKICK_13 (Color.TYPE_13), // INFO:
    // TODO: sidekick that generates a single Caps ?
    ;

    private final Color type;

    Sidekick(Color type) {
        this.type = type;
    }

    public static Color random(Set<Sidekick> sidekicks) {
        if (sidekicks.isEmpty()) {
            throw new IllegalArgumentException("Can't get a type from empty set.");
        }
        return sidekicks.stream()
                 .findAny()
                 .get().type
          ;
        // return Sidekick.BLANK_1.type;
    }

    public Color color() {
        return type;
    }

}
