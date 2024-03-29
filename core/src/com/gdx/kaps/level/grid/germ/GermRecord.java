package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.Utils;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Grid;

import java.util.Objects;
import java.util.function.BiConsumer;

import static com.gdx.kaps.level.grid.caps.EffectAnim.EffectType.SLICE_FX;

public enum GermRecord {
    BASIC("basic", 8, 1, 1),
    WALL("wall", 4, 4, 4),
    VIRUS("virus", 8, 1, 3, 8, (lvl, gm) -> lvl.applyToGrid(GermRecord::contamineRandomCaps, gm), "virus"),
    THORN("thorn", 8, 1, 2, 5, (lvl, gm) -> lvl.applyToGrid(GermRecord::hitRandomCaps, gm), "slice"),
    ;

    private final BiConsumer<Level, Germ> power;
    private final String name;
    private final String sound;
    private final int cooldown;
    private final int nbFrames;
    private final int maxHP;
    private final int mana;

    GermRecord(String name, int frames, int maxHP, int mana) {
        this(name, frames, maxHP, mana, 0, (gd, gm) -> {}, "plop");
    }

    GermRecord(String name, int frames, int maxHP, int mana, int cooldown, BiConsumer<Level, Germ> power, String sound) {
        Objects.requireNonNull(name);
        if (maxHP < 0) throw new IllegalArgumentException("Germ can't have a negative health (" + maxHP + ")");
        if (cooldown < 0) throw new IllegalArgumentException("Germ can't have a negative cooldown (" + cooldown + ")");

        this.cooldown = cooldown;
        this.nbFrames = frames;
        this.sound = sound;
        this.power = power;
        this.maxHP = maxHP;
        this.mana = mana;
        this.name = name;
    }

    public BiConsumer<Level, Germ> power() {
        return power;
    }

    public int mana() {
        return mana;
    }

    public int maxHP() {
        return maxHP;
    }

    public int nbFrames() {
        return nbFrames;
    }

    public int cooldown() {
        return cooldown;
    }

    public String sound() {
        return sound;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Turns (at most) one random caps from the grid into a {@link VirusGerm}.
     * @param grid the affected grid
     */
    private static void contamineRandomCaps(Grid grid, Germ germ) {
        grid.pickRandomCaps().ifPresent(grid::contamine);
    }

    /**
     * Hits (at most) one random caps among the 9 tiles around the germ.
     * @param grid the affected grid
     */
    private static void hitRandomCaps(Grid grid, Germ germ) {
        var capsAround = grid.everyCapsInGrid()
                           .filter(o ->
                                     -1 <= germ.x() - o.x() && germ.x() - o.x() <= 1 &&
                                       -1 <= germ.y() - o.y() && germ.y() - o.y() <= 1
                           );
        Utils.getRandomFrom(capsAround).ifPresent(caps -> grid.hit(caps.x(), caps.y(), SLICE_FX));
    }
}
