package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.grid.Color;

import java.util.Set;

public enum SidekickRecord {
    // IMPL: make it a class + SidekickRecord
    //  extends 'animated'.

    // TODO: add powers
    SEAN   (Color.COLOR_1, "Sean"),    // INFO: Hits a tile then its neighbors
    ZYRAME (Color.COLOR_2, "Zyrame"),  // INFO: Hits 2 random germs
    RED    (Color.COLOR_3, "Red"),     // INFO: Slices a.random column
    MIMAPS (Color.COLOR_4, "Mimaps"),  // INFO: Hits 3 random.tiles
    PAINT  (Color.COLOR_5, "Paint"),   // INFO: Paints 5 tiles in mate's color
    XERETH (Color.COLOR_6, "Xereth"),  // INFO: Slices two.diagonals
    JIM    (Color.COLOR_10, "Jim"),    // INFO: Slices a.random line
    COLOR  (Color.COLOR_11, "Color"),  // INFO: Generates a.single-colored gelule
    // TODO: sidekick that generates a single Caps ?
    ;

    private final String name;
    private final Color type;

    SidekickRecord(Color type, String name) {
        this.name = name;
        this.type = type;
    }

    public static Color random(Set<SidekickRecord> sidekicks) {
        if (sidekicks.isEmpty()) {
            throw new IllegalArgumentException("Can't get a type from empty set.");
        }
        return sidekicks.stream()
                 .findAny()
                 .get().type
          ;
    }

    public Color color() {
        return type;
    }

    public String path() {
        return name;
    }
}
