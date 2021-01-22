package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.caps.Gelule;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public enum SidekickRecord {
    SEAN("Sean", Color.COLOR_1, "fire", (lvl) -> {}, 20),              // INFO: Hits a tile then its neighbors
    ZYRAME("Zyrame", Color.COLOR_2, "slice", (lvl) -> {}, 20, 2),   // INFO: Hits 2 random germs
    RED("Red", Color.COLOR_3, "slice", (lvl) -> hitRandomColumn(lvl.grid()), 20, 2),
    MIMAPS("Mimaps", Color.COLOR_4, "fire", (lvl) -> {}, 15, 2),   // INFO: Hits 3 random.tiles
    PAINT("Paint", Color.COLOR_5, "paint", (lvl) -> {}, 10),             // INFO: Paints 5 tiles in mate's color
    XERETH("Xereth", Color.COLOR_6, "slice", (lvl) -> {}, 25),            // INFO: Slices two.diagonals
    JIM("Jim", Color.COLOR_10, "slice", (lvl) -> hitRandomLine(lvl.grid()), 25),
    COLOR("Color", Color.COLOR_11,"gen", SidekickRecord::generateSingleColoredGelule, -5),
    // TODO: sidekick that generates a single Caps ?
    ;

    private final Consumer<Level> power;
    private final Color type;
    private final String name;
    private final String sound;
    private final int cooldown;
    private final int maxMana;
    private final int damage;

    SidekickRecord(String name, Color type, String soundName, Consumer<Level> power, int max) {
        this(name, type, soundName, power, max, 1);
    }

    SidekickRecord(String name, Color type, String soundName, Consumer<Level> power, int max, int dmg) {
        this.power = power;
        this.name = name;
        this.type = type;
        // INFO: negative max = cooldown
        maxMana = Math.max(max, 0);
        cooldown = -Math.min(max, 0);
        damage = dmg;
        sound = soundName;
    }

    public static SidekickRecord ofName(String sdkName) {
        return valueOf(sdkName.toUpperCase());
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

    public String sound() {
        return sound;
    }

    // powers

    private static void generateSingleColoredGelule(Level lvl) {
        lvl.setNext(2, new Gelule(lvl, Color.random(lvl)));
    }

    private static void hitRandomLine(Grid grid) {
        grid.pickRandomObject().ifPresent(
          obj -> IntStream.range(0, grid.width())
                   .forEach(n -> grid.hit(n, obj.y()))
        );
    }

    private static void hitRandomColumn(Grid grid) {
        grid.pickRandomObject().ifPresent(
          obj -> IntStream.range(0, grid.height())
                   .forEach(n -> grid.hit(obj.x(), n))
        );
    }
}
