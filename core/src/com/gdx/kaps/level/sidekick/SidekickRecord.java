package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.caps.EffectAnim;
import com.gdx.kaps.level.grid.caps.Gelule;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.gdx.kaps.Utils.getRandomFrom;
import static com.gdx.kaps.level.grid.caps.EffectAnim.EffectType.*;
import static java.util.stream.Collectors.toList;

public enum SidekickRecord {
    SEAN("Sean", Color.COLOR_1, "fire", (lvl) -> lvl.applyToGrid(SidekickRecord::hitRandomTileAndAdjacents), 20),
    ZYRAME("Zyrame", Color.COLOR_2, "slice", (lvl) -> lvl.applyToGrid(SidekickRecord::hitTwoRandomGerms), 20),
    RED("Red", Color.COLOR_3, "slice", (lvl) -> lvl.applyToGrid(SidekickRecord::sliceRandomColumn), 20),
    MIMAPS("Mimaps", Color.COLOR_4, "fire", (lvl) -> lvl.applyToGrid(SidekickRecord::hitThreeRandomTiles), 15),
    PAINT("Paint", Color.COLOR_5, "paint", SidekickRecord::repaintFiveTiles, 10),
    XERETH("Xereth", Color.COLOR_6, "slice", (lvl) -> lvl.applyToGrid(SidekickRecord::sliceRandomDiagonals), 25),
    JIM("Jim", Color.COLOR_10, "slice", (lvl) -> lvl.applyToGrid(SidekickRecord::sliceRandomLine), 25),
    COLOR("Color", Color.COLOR_11,"gen", SidekickRecord::generateSingleColoredGelule, -5),
    // TODO: sidekick that burns 1 germ, 3dmg, 15 mana
    // TODO: sidekick that generates a single Caps ?
    ;

    private final Consumer<Level> power;
    private final Color type;
    private final String name;
    private final String sound;
    private final int cooldown;
    private final int maxMana;

    SidekickRecord(String name, Color type, String soundName, Consumer<Level> power, int mana) {
        this.power = power;
        this.name = name;
        this.type = type;
        // INFO: negative mana = cooldown
        maxMana = mana > 0 ? 4:0;//TMP: Math.max(mana, 0);
        cooldown = -Math.min(mana, 0);
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

    public String sound() {
        return sound;
    }

    // powers

    private static void generateSingleColoredGelule(Level lvl) {
        // TODO: make it the color of the sidekick/mate
        lvl.setNext(2, new Gelule(lvl, Color.random(lvl)));
    }

    private static void repaintFiveTiles(Level lvl) {
        var color = getRandomFrom(
          lvl.sidekicks().stream()
            .map(Sidekick::color)
            .filter(c -> c != PAINT.color())
            .collect(toList())
        ).orElse(PAINT.color());

        for (int i = 0; i < 5; i++) {
            lvl.applyToGrid(grid -> getRandomFrom(
              grid.everyObjectInGrid()
                .filter(obj -> !obj.color().equals(color))
                .filter(obj -> !obj.isGerm())
                .collect(toList())
            ).ifPresent(caps -> {
                caps.paint(color);
                grid.addEffect(EffectAnim.EffectType.PAINT, caps);
            }));
        }
    }

    private static void sliceRandomLine(Grid grid) {
        grid.pickRandomObject().ifPresent(
          obj -> IntStream.range(0, grid.width())
                   .forEach(n -> grid.hit(n, obj.y(), SLICE))
        );
    }

    private static void sliceRandomColumn(Grid grid) {
        grid.pickRandomObject().ifPresent(
          obj -> IntStream.range(0, grid.height())
                   .forEach(n -> grid.hit(obj.x(), n, SLICE, 2))
        );
    }

    private static void sliceRandomDiagonals(Grid grid) {
        grid.pickRandomObject().ifPresent(
          obj -> {
              grid.hit(obj.x(), obj.y(), SLICE);
              for (int i = 0; i < grid.width(); i++) {
                  grid.hit(obj.x() - i, obj.y() - i, SLICE);
                  grid.hit(obj.x() - i, obj.y() + i, SLICE);
                  grid.hit(obj.x() + i, obj.y() - i, SLICE);
                  grid.hit(obj.x() + i, obj.y() + i, SLICE);
              }
          }
        );
    }

    private static void hitRandomTileAndAdjacents(Grid grid) {
        grid.pickRandomObject().ifPresent(
          obj -> {
              grid.hit(obj.x(), obj.y(), FIRE, 2);
              grid.hit(obj.x() - 1, obj.y(), CORE);
              grid.hit(obj.x() + 1, obj.y(), CORE);
              grid.hit(obj.x(), obj.y() + 1, CORE);
              grid.hit(obj.x(), obj.y() - 1, CORE);
          }
        );
    }

    private static void hitThreeRandomTiles(Grid grid) {
        var objects = grid.everyObjectInGrid().collect(toList());
        Collections.shuffle(objects);
        objects.stream()
          .limit(3)
          .forEach(obj -> grid.hit(obj.x(), obj.y(), FIRE, 2));
    }

    private static void hitTwoRandomGerms(Grid grid) {
        for (int i = 0; i < 2; i++) {
            grid.pickRandomGerm()
              .ifPresent(germ -> grid.hit(germ.x(), germ.y(), SLICE, 2));
        }
        // TODO: powers: add deleted to matching sidekick
    }
}
