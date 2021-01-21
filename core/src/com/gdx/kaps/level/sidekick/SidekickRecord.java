package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.caps.Gelule;

import java.util.function.Consumer;

public enum SidekickRecord {
    // TODO: add powers
    SEAN   (Color.COLOR_1, "Sean", (lvl) -> {}, 20),              // INFO: Hits a tile then its neighbors
    ZYRAME (Color.COLOR_2, "Zyrame", (lvl) -> {}, 20, 2),   // INFO: Hits 2 random germs
    RED    (Color.COLOR_3, "Red", (lvl) -> {}, 20, 2),      // INFO: Slices a.random column
    MIMAPS (Color.COLOR_4, "Mimaps", (lvl) -> {}, 15, 2),   // INFO: Hits 3 random.tiles
    PAINT  (Color.COLOR_5, "Paint", (lvl) -> {}, 10),             // INFO: Paints 5 tiles in mate's color
    XERETH (Color.COLOR_6, "Xereth", (lvl) -> {}, 25),            // INFO: Slices two.diagonals
    JIM    (Color.COLOR_10, "Jim", (lvl) -> {}, 25),              // INFO: Slices a.random line
    COLOR  (Color.COLOR_11, "Color", SidekickRecord::generateSingleColoredGelule, -5),             // INFO: Generates a.single-colored gelule
    // TODO: sidekick that generates a single Caps ?
    ;

    private final Consumer<Level> power;
    private final String name;
    private final int cooldown;
    private final int maxMana;
    private final int damage;
    private final Color type;

    SidekickRecord(Color type, String name, Consumer<Level> power, int max) {
        this(type, name, power, max, 1);
    }

    SidekickRecord(Color type, String name, Consumer<Level> power, int max, int dmg) {
        this.power = power;
        this.name = name;
        this.type = type;
        // INFO: negative max = cooldown
        maxMana = Math.max(max, 0);
        cooldown = -Math.min(max, 0);
        damage = dmg;
    }

    public Consumer<Level> power() {
        return power;
    }

    public String path() {
        return name;
    }

    public Color color() {
        return type;
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

    //

    private static void generateSingleColoredGelule(Level lvl) {
        lvl.setNext(2, new Gelule(lvl, Color.random(lvl)));
    }
}
