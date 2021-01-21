package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.grid.Color;

public enum SidekickRecord {
    // TODO: add powers
    SEAN   (Color.COLOR_1, "Sean", 20),              // INFO: Hits a tile then its neighbors
    ZYRAME (Color.COLOR_2, "Zyrame", 20, 2),   // INFO: Hits 2 random germs
    RED    (Color.COLOR_3, "Red", 20, 2),      // INFO: Slices a.random column
    MIMAPS (Color.COLOR_4, "Mimaps", 15, 2),   // INFO: Hits 3 random.tiles
    PAINT  (Color.COLOR_5, "Paint", 10),             // INFO: Paints 5 tiles in mate's color
    XERETH (Color.COLOR_6, "Xereth", 25),            // INFO: Slices two.diagonals
    JIM    (Color.COLOR_10, "Jim", 25),              // INFO: Slices a.random line
    COLOR  (Color.COLOR_11, "Color", -5),             // INFO: Generates a.single-colored gelule
    // TODO: sidekick that generates a single Caps ?
    ;

    private final String name;
    private final int cooldown;
    private final int maxMana;
    private final int damage;
    private final Color type;

    SidekickRecord(Color type, String name, int max) {
        this(type, name, max, 1);
    }

    SidekickRecord(Color type, String name, int max, int dmg) {
        this.name = name;
        this.type = type;
        // INFO: negative max = cooldown
        maxMana = Math.max(max, 0);
        cooldown = -Math.min(max, 0);
        damage = dmg;
    }

    public Color color() {
        return type;
    }

    public String path() {
        return name;
    }

    public int maxMana() {
        return maxMana;
    }

    public int cooldown() {
        return cooldown;
    }

    public int damage() {
        return damage;
    }
}
