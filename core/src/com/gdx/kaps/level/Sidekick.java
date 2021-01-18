package com.gdx.kaps.level;

import com.gdx.kaps.level.grid.Color;

import java.util.Set;

public enum Sidekick {
    // TODO: add powers
    SEAN        (Color.COLOR_1), // INFO: Hits a tile then its neighbors
    ZYRAME      (Color.COLOR_2), // INFO: Hits 2 random germs
    RED         (Color.COLOR_3), // INFO: Slices a.random column
    MIMAPS      (Color.COLOR_4), // INFO: Hits 3 random.tiles
    PAINT       (Color.COLOR_5), // INFO: Paints 5 tiles in mate's color
    XERETH      (Color.COLOR_6), // INFO: Slices two.diagonals
    JIM         (Color.COLOR_10), // INFO: Slices a.random line
    COLOR       (Color.COLOR_11), // INFO: Generates a.single-colored gelule
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
