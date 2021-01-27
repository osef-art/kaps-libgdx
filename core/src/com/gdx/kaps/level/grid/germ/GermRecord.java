package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.Utils;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.caps.EffectAnim;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public enum GermRecord {
    BASIC("basic", 8, 1),
    WALL("wall", 4, 4),
    VIRUS("virus", 8, 1, 7, (lvl, gm) -> lvl.applyToGrid(GermRecord::contamineRandomCaps, gm), "virus"),
    THORN("thorn", 8, 1, 5, (lvl, gm) -> lvl.applyToGrid(GermRecord::hitRandomCaps, gm), "slice"),
    ;

    private final BiConsumer<Level, Germ> power;
    private final String name;
    private final String sound;
    private final int cooldown;
    private final int nbFrames;
    private final int maxHP;

    GermRecord(String name, int frames, int maxHP) {
        this(name, frames, maxHP, 0, (gd, gm) -> {}, "plop");
    }

    GermRecord(String name, int frames, int maxHP, int cooldown, BiConsumer<Level, Germ> power, String sound) {
        Objects.requireNonNull(name);
        if (maxHP < 0) throw new IllegalArgumentException("Germ can't have a negative health (" + maxHP + ")");
        if (cooldown < 0) throw new IllegalArgumentException("Germ can't have a negative cooldown (" + cooldown + ")");

        this.cooldown = cooldown;
        this.nbFrames = frames;
        this.sound = sound;
        this.power = power;
        this.maxHP = maxHP;
        this.name = name;
    }

    public BiConsumer<Level, Germ> power() {
        return power;
    }

    public int maxHP() {
        return maxHP;
    }

    public String type() {
        return name;
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
        var capsAround = grid.everyObjectInGrid()
                           .filter(o -> -1 <= germ.x() - o.x() && germ.x() - o.x() <= 1 &&
                                          -1 <= germ.y() - o.y() && germ.y() - o.y() <= 1
                           )
                           //.peek(System.out::println)
                           .filter(o -> !o.isGerm())
                           .collect(Collectors.toList());
        Utils.getRandomFrom(capsAround).ifPresent(caps -> {
            grid.hit(caps);
            grid.addEffect(EffectAnim.EffectType.SLICE_FX, caps.x(), caps.y());
        });
    }
}
