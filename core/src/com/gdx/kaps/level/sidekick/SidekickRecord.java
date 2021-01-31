package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.caps.Caps;
import com.gdx.kaps.level.grid.caps.EffectAnim;
import com.gdx.kaps.level.grid.caps.EffectAnim.EffectType;
import com.gdx.kaps.level.grid.caps.Gelule;
import com.gdx.kaps.renderer.Zone;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static com.gdx.kaps.MainScreen.dim;
import static com.gdx.kaps.Utils.getRandomFrom;
import static com.gdx.kaps.level.grid.caps.EffectAnim.EffectType.*;
import static java.util.stream.Collectors.toList;

public enum SidekickRecord {
    SEAN("Sean", Color.COLOR_1, "fire", FIRE_FX, Target.RANDOM_GRID_OBJECT, 5, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitRandomTileAndAdjacents, sdk), 20, 2),
    ZYRAME("Zyrame", Color.COLOR_2, "slice", SLICE_FX, Target.RANDOM_GERM, 2, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitTwoRandomGerms, sdk), 20, 2),
    RED("Red", Color.COLOR_3, "slice", SLICE_FX, Target.RANDOM_GRID_OBJECT, 10, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomColumn, sdk), 20, 2),
    MIMAPS("Mimaps", Color.COLOR_4, "fire", FIRE_FX, Target.RANDOM_GRID_OBJECT, 3, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitThreeRandomTiles, sdk), 15, 2),
    PAINT("Paint", Color.COLOR_5, "gen", PAINT_FX, Target.RANDOM_CAPS, 5, SidekickRecord::repaintFiveCaps, 10, 0),
    XERETH("Xereth", Color.COLOR_6, "slice", SLICE_FX, Target.RANDOM_GRID_OBJECT, 11, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomDiagonals, sdk), 25),
    BOMBER("Bomb", Color.COLOR_7, "color", CORE_FX, Target.TARGETED, 9, SidekickRecord::generateBombedGelule, -13),
    JIM("Jim", Color.COLOR_10, "slice", SLICE_FX, Target.RANDOM_GRID_OBJECT, 6, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::sliceRandomLine, sdk), 20),
    COLOR("Color", Color.COLOR_11,"color", CORE_FX, Target.TARGETED, 0, SidekickRecord::generateSingleColoredGelule, -4, 0),
    SNIPER("Punch", Color.COLOR_12,"paint", CORE_FX, Target.RANDOM_GERM, 1, (lvl, sdk) -> lvl.applyToGrid(SidekickRecord::hitRandomGerm, sdk), 15, 3),
    // TODO: sidekick that generates a single Caps ?
    ;

    private enum Target {
        RANDOM_TILE,
        RANDOM_CAPS,
        RANDOM_GRID_OBJECT,
        RANDOM_GERM,
        TARGETED
    }
    public static class Stats {
        private final LinkedHashMap<String, Integer> attributes = new LinkedHashMap<>();
        private final int stars;

        public Stats(int damage, int maxCaps, Target target, int mana) {
            int amount;
            switch (maxCaps) {
                case 0:
                    amount = 0;
                    break;
                case 1:
                    amount = 1;
                    break;
                case 2:
                case 3:
                case 4:
                    amount = 2;
                    break;
                case 5:
                case 6:
                case 7:
                    amount = 3;
                    break;
                default:
                    amount = 4;
            }
            attributes.put("damage", damage);
            attributes.put("amount", amount);
            attributes.put("precision", target.ordinal());
            attributes.put("frequency", (mana  < 0) ? (mana + 16) / 3 : (30 - mana) / 5);

            if (attributes.get("damage") < 0 || 4 < attributes.get("damage"))
                throw new IllegalStateException("Invalid damage value: " + damage);
            if (maxCaps < 0 || 4 < attributes.get("amount"))
                throw new IllegalStateException("Invalid amount value: " + amount);
            if (4 < attributes.get("precision"))
                throw new IllegalStateException("Invalid precision value: " + attributes.get("precision"));
            if (attributes.get("frequency") < 1 || 4 < attributes.get("frequency"))
                throw new IllegalStateException("Invalid frequency value: " + attributes.get("frequency"));

            int points = attributes.values().stream().reduce(0, Integer::sum);
            stars = (points - 4) / 2;

            if (stars < 1 || 3 < stars)
                throw new IllegalStateException("Invalid number of attributes: " + points + " (" + stars + " stars)");
        }

        public ArrayList<Map.Entry<String, Integer>> list() {
            return new ArrayList<>(attributes.entrySet());
        }

        public int stars() {
            return stars;
        }
    }
    private final BiConsumer<Level, SidekickRecord> power;
    private final EffectType effect;
    private final Color type;
    private final Stats stats;
    private final String name;
    private final String sound;
    private final int cooldown;
    private final int maxMana;
    private final int damage;

    SidekickRecord(String name, Color type, String soundName, EffectType effect, Target target, int maxCaps, BiConsumer<Level, SidekickRecord> power, int mana) {
        this(name, type, soundName, effect, target, maxCaps, power, mana, 1);
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
    SidekickRecord(String name, Color color, String soundName, EffectType effect, Target target, int maxCaps, BiConsumer<Level, SidekickRecord> power, int mana, int dmg) {
        this.effect = effect;
        this.power = power;
        this.name = name;
        this.type = color;
        sound = soundName;
        damage = dmg;
        maxMana = Math.max(mana, 0);
        cooldown = -Math.min(mana, 0);
        stats = new Stats(dmg, maxCaps, target, mana);
    }

    public static SidekickRecord ofName(String sdkName) {
        return valueOf(sdkName.toUpperCase());
    }

    public BiConsumer<Level, SidekickRecord> power() {
        return power;
    }

    public EffectType effect() {
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

    public Color color() {
        return type;
    }

    public Stats stats() {
        return stats;
    }

    public String sound() {
        return sound;
    }

    @Override
    public String toString() {
        return name;
    }

    // powers

    /**
     * Sets the next upcoming gelule to a gelule with both caps of the same color.
     * @param lvl the current level
     * @param sidekick the attacking sidekick's {@link SidekickRecord}
     */
    private static void generateSingleColoredGelule(Level lvl, SidekickRecord sidekick) {
        lvl.setNext(2, Gelule.singleColored(lvl, lvl.getSidekickExcept(sidekick).color()));
        Level.addEffect(new EffectAnim.EffectAnimBuilder(CORE_FX, dim.get(Zone.NEXT_BOX)).withSpeed(50_000_000).build());
    }

    private static void generateBombedGelule(Level lvl, SidekickRecord sidekick) {
        lvl.setNext(2, Gelule.withPower(lvl, Caps.Type.BOMB));
        // TODO: replace setNext by "push/shiftNext"
        //  -> doesn't replace the current next but move it to next2
        Level.addEffect(new EffectAnim.EffectAnimBuilder(CORE_FX, dim.get(Zone.NEXT_BOX)).withSpeed(50_000_000).build());
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
              grid.hit(obj.x() - 1, obj.y(), CORE_FX);
              grid.hit(obj.x() + 1, obj.y(), CORE_FX);
              grid.hit(obj.x(), obj.y() + 1, CORE_FX);
              grid.hit(obj.x(), obj.y() - 1, CORE_FX);
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
        // IMPL: make power methods unique, w/ their own damage and effects ?
        for (int i = 0; i < 2; i++) {
            hitRandomGerm(grid, sidekick);
        }
    }
}
