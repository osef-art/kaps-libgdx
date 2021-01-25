package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.caps.EffectAnim;
import com.gdx.kaps.level.grid.caps.Gelule;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static com.gdx.kaps.Utils.getRandomFrom;
import static com.gdx.kaps.level.grid.caps.EffectAnim.EffectType.*;
import static java.util.stream.Collectors.toList;

public enum SidekickRecord {
    SEAN("Sean", Color.COLOR_1, "fire", (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitRandomTileAndAdjacents, sdk), 20),
    ZYRAME("Zyrame", Color.COLOR_2, "slice", (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitTwoRandomGerms, sdk), 20, 2),
    RED("Red", Color.COLOR_3, "slice", (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomColumn, sdk), 20, 2),
    MIMAPS("Mimaps", Color.COLOR_4, "fire", (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitThreeRandomTiles, sdk), 15, 2),
    PAINT("Paint", Color.COLOR_5, "paint", SidekickRecord::repaintFiveTiles, 10),
    XERETH("Xereth", Color.COLOR_6, "slice", (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomDiagonals, sdk), 25),
    JIM("Jim", Color.COLOR_10, "slice", (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomLine, sdk), 25),
    COLOR("Color", Color.COLOR_11,"gen", SidekickRecord::generateSingleColoredGelule, -5),
    // TODO: sidekick that burns 1 germ, 3dmg, 15 mana
    // TODO: sidekick that generates a single Caps ?
    ;

    private final BiConsumer<Level, SidekickRecord> power;
    private final Color type;
    private final String name;
    private final String sound;
    private final int cooldown;
    private final int maxMana;
    private final int damage;

    SidekickRecord(String name, Color type, String soundName, BiConsumer<Level, SidekickRecord> power, int mana) {
        this(name, type, soundName, power, mana, 1);
    }

    SidekickRecord(String name, Color type, String soundName, BiConsumer<Level, SidekickRecord> power, int mana, int dmg) {
        this.power = power;
        this.name = name;
        this.type = type;
        // INFO: negative mana = cooldown (put in doc)
        // TEST maxMana = Math.max(mana, 0);
        maxMana = mana > 0 ? 4 : mana;
        cooldown = -Math.min(mana, 0);
        sound = soundName;
        damage = dmg;
    }

    public static SidekickRecord ofName(String sdkName) {
        return valueOf(sdkName.toUpperCase());
    }

    public BiConsumer<Level, SidekickRecord> power() {
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

    public String sound() {
        return sound;
    }

    // powers

    private static void generateSingleColoredGelule(Level lvl, SidekickRecord sidekick) {
        lvl.setNext(2, new Gelule(lvl, lvl.sidekickExcept(COLOR).color()));
    }

    private static void repaintFiveTiles(Level lvl, SidekickRecord sidekick) {
        for (int i = 0; i < 5; i++) {
            lvl.applyToGrid(SidekickRecord::repaintRandomTile, sidekick);
        }
    }

    private static void repaintRandomTile(Grid grid, SidekickRecord sidekick) {
        getRandomFrom(
          grid.everyObjectInGrid()
            .filter(obj -> !obj.color().equals(sidekick.color()))
            .filter(obj -> !obj.isGerm())
            .collect(toList())
        ).ifPresent(caps -> {
            caps.paint(sidekick.color());
            grid.addEffect(EffectAnim.EffectType.PAINT, caps);
        });
    }

    private static void sliceRandomLine(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomObject().ifPresent(
          obj -> IntStream.range(0, grid.width())
                   .forEach(n -> grid.hit(n, obj.y(), sidekick.damage, SLICE))
        );
    }

    private static void sliceRandomColumn(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomObject().ifPresent(
          obj -> IntStream.range(0, grid.height())
                   .forEach(n -> grid.hit(obj.x(), n, sidekick.damage, SLICE))
        );
    }

    private static void sliceRandomDiagonals(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomObject().ifPresent(
          obj -> {
              grid.hit(obj.x(), obj.y(), sidekick.damage, SLICE);
              for (int i = 0; i < grid.width(); i++) {
                  grid.hit(obj.x() - i, obj.y() - i, sidekick.damage, SLICE);
                  grid.hit(obj.x() - i, obj.y() + i, sidekick.damage, SLICE);
                  grid.hit(obj.x() + i, obj.y() - i, sidekick.damage, SLICE);
                  grid.hit(obj.x() + i, obj.y() + i, sidekick.damage, SLICE);
              }
          }
        );
    }

    private static void hitRandomTileAndAdjacents(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomObject().ifPresent(
          obj -> {
              grid.hit(obj.x(), obj.y(), sidekick.damage, FIRE);
              grid.hit(obj.x() - 1, obj.y(), sidekick.damage - 1, CORE);
              grid.hit(obj.x() + 1, obj.y(), sidekick.damage - 1, CORE);
              grid.hit(obj.x(), obj.y() + 1, sidekick.damage - 1, CORE);
              grid.hit(obj.x(), obj.y() - 1, sidekick.damage - 1, CORE);
          }
        );
    }

    private static void hitThreeRandomTiles(Grid grid, SidekickRecord sidekick) {
        var objects = grid.everyObjectInGrid().collect(toList());
        Collections.shuffle(objects);
        objects.stream()
          .limit(3)
          .forEach(obj -> grid.hit(obj.x(), obj.y(), sidekick.damage, FIRE));
    }

    private static void hitTwoRandomGerms(Grid grid, SidekickRecord sidekick) {
        for (int i = 0; i < 2; i++) {
            grid.pickRandomGerm()
              .ifPresent(germ -> grid.hit(germ.x(), germ.y(), sidekick.damage, SLICE));
        }
        // TODO: powers: add deleted to matching sidekick
    }
}
