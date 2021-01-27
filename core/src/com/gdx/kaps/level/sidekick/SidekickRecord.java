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
    SEAN("Sean", Color.COLOR_1, "fire", FIRE_FX, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitRandomTileAndAdjacents, sdk), 20),
    ZYRAME("Zyrame", Color.COLOR_2, "slice", SLICE_FX, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitTwoRandomGerms, sdk), 20, 2),
    RED("Red", Color.COLOR_3, "slice", SLICE_FX, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomColumn, sdk), 20, 2),
    MIMAPS("Mimaps", Color.COLOR_4, "fire", FIRE_FX, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitThreeRandomTiles, sdk), 15, 2),
    PAINT("Paint", Color.COLOR_5, "gen", PAINT_FX, SidekickRecord::repaintFiveCaps, 10),
    XERETH("Xereth", Color.COLOR_6, "slice", SLICE_FX, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomDiagonals, sdk), 25),
    JIM("Jim", Color.COLOR_10, "slice", SLICE_FX, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomLine, sdk), 25),
    COLOR("Color", Color.COLOR_11,"color", CORE_FX, SidekickRecord::generateSingleColoredGelule, -5),
    PUNCH("Punch", Color.COLOR_12,"paint", CORE_FX, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitRandomGerm, sdk), 15, 3),
    // TODO: sidekick that generates a single Caps ?
    ;

    private final BiConsumer<Level, SidekickRecord> power;
    private final EffectAnim.EffectType effect;
    private final Color type;
    private final String name;
    private final String sound;
    private final int cooldown;
    private final int maxMana;
    private final int damage;

    SidekickRecord(String name, Color type, String soundName, EffectAnim.EffectType effect, BiConsumer<Level, SidekickRecord> power, int mana) {
        this(name, type, soundName, effect, power, mana, 1);
    }

    /**
     * Instantiates a sidekick record
     * @param name its name, useful to find its path among img textures
     * @param color its color from the Record of playable colors
     * @param soundName the name of the sound played makes when it attacks      // IMPL: to be moved in effect anim ?
     * @param effect the type of effect produced when it attacks
     * @param power it's effect on the level/board when it attacks
     * @param mana the number of consumed caps needed to trigger its power.
     *             if the value is negative, stands for the number of turns (cooldown) until triggering.
     * @param dmg the number of damage
     */
    SidekickRecord(String name, Color color, String soundName, EffectAnim.EffectType effect, BiConsumer<Level, SidekickRecord> power, int mana, int dmg) {
        this.effect = effect;
        this.power = power;
        this.name = name;
        this.type = color;
        maxMana = Math.max(mana, 0);
        // TEST  maxMana = mana > 0 ? 4 : mana;
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

    public EffectAnim.EffectType effect() {
        return effect;
    }

    public int cooldown() {
        return cooldown;
    }

    public int maxMana() {
        return maxMana;
    }

    public int damage() {
        return damage;
    }

    public String path() {
        return name;
    }

    public Color color() {
        return type;
    }

    public String sound() {
        return sound;
    }

    // powers

    /**
     * Sets the next upcoming gelule to a gelule with both caps of the same color.
     * @param lvl the current level
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void generateSingleColoredGelule(Level lvl, SidekickRecord sidekick) {
        lvl.setNext(2, new Gelule(lvl, lvl.getSidekickExcept(sidekick).color()));
    }

    /**
     * Paints (at most) five random caps of the color of one of
     * the {@code sidekick}'s mates in the current {@code lvl}.
     * @param lvl the current level
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void repaintFiveCaps(Level lvl, SidekickRecord sidekick) {
        var color = lvl.getSidekickExcept(sidekick).color();

        for (int i = 0; i < 5; i++) {
            lvl.applyToGrid((grid, sdk) -> getRandomFrom(
              grid.everyObjectInGrid()
                .filter(obj -> !obj.color().equals(color))
                .filter(obj -> !obj.isGerm())
                .collect(toList())
            ).ifPresent(caps -> grid.paint(caps.x(), caps.y(), color)), sidekick);
        }
    }

    /**
     * Hits a random object from the grid and all objects on the same line.
     * @param grid the affected grid
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void sliceRandomLine(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomObject().ifPresent(
          obj -> IntStream.range(0, grid.width())
                   .forEach(n -> grid.hit(n, obj.y(), sidekick))
        );
    }

    /**
     * Hits a random object from the grid and all objects on the same column.
     * @param grid the affected grid
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void sliceRandomColumn(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomObject().ifPresent(
          obj -> IntStream.range(0, grid.height())
                   .forEach(n -> grid.hit(obj.x(), n, sidekick))
        );
    }

    /**
     * Hits a random object from the grid and all objects on the same diagonals (X)
     * @param grid the affected grid
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void sliceRandomDiagonals(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomObject().ifPresent(
          obj -> {
              grid.hit(obj.x(), obj.y(), sidekick);
              for (int i = 0; i < grid.width(); i++) {
                  grid.hit(obj.x() - i, obj.y() - i, sidekick);
                  grid.hit(obj.x() - i, obj.y() + i, sidekick);
                  grid.hit(obj.x() + i, obj.y() - i, sidekick);
                  grid.hit(obj.x() + i, obj.y() + i, sidekick);
              }
          }
        );
    }

    /**
     * Hits a random object from the grid and the 4 objects at left, right, top and bottom.
     * @param grid the affected grid
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void hitRandomTileAndAdjacents(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomObject().ifPresent(
          obj -> {
              grid.hit(obj.x(), obj.y(), sidekick);
              grid.hit(obj.x() - 1, obj.y());
              grid.hit(obj.x() + 1, obj.y());
              grid.hit(obj.x(), obj.y() + 1);
              grid.hit(obj.x(), obj.y() - 1);
              grid.addEffect(CORE_FX, obj.x() - 1, obj.y());
              grid.addEffect(CORE_FX, obj.x() + 1, obj.y());
              grid.addEffect(CORE_FX, obj.x(), obj.y() + 1);
              grid.addEffect(CORE_FX, obj.x(), obj.y() - 1);
          }
        );
    }

    /**
     * Hits (at most) three random objects from the grid
     * @param grid the affected grid
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void hitThreeRandomTiles(Grid grid, SidekickRecord sidekick) {
        var objects = grid.everyObjectInGrid().collect(toList());
        Collections.shuffle(objects);
        objects.stream()
          .limit(3)
          .forEach(obj -> grid.hit(obj.x(), obj.y(), sidekick));
    }

    /**
     * Hits a random germ from the grid
     * @param grid the affected grid
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void hitRandomGerm(Grid grid, SidekickRecord sidekick) {
        grid.pickRandomGerm()
          .ifPresent(germ -> grid.hit(germ.x(), germ.y(), sidekick));
    }

    /**
     * Hits (at most) two random germs from the grid.
     * @param grid the affected grid
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void hitTwoRandomGerms(Grid grid, SidekickRecord sidekick) {
        // TODO: make power     methods unique, w/ their own damage and effects
        for (int i = 0; i < 2; i++) {
            hitRandomGerm(grid, sidekick);
        }
    }
}
