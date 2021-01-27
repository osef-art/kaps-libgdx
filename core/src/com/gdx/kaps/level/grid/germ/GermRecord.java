package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.grid.Grid;

import java.util.Objects;
import java.util.function.Consumer;

public enum GermRecord {
    // TODO: handle germ powers
    BASIC("basic", 8, 1),
    WALL("wall", 4, 4),
    VIRUS("virus", 8, 1, 7, GermRecord::contamineRandomCaps, "virus"),
    THORN("thorn", 8, 1, 5, GermRecord::hitRandomCaps, "slice"),
    ;

    private final Consumer<Grid> power;
    private final String name;
    private final String sound;
    private final int cooldown;
    private final int nbFrames;
    private final int maxHP;

    GermRecord(String name, int frames, int maxHP) {
        this(name, frames, maxHP, 0, (g) -> {}, "plop");
    }

    GermRecord(String name, int frames, int maxHP, int cooldown, Consumer<Grid> power, String sound) {
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

    public Consumer<Grid> power() {
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

    private static void contamineRandomCaps(Grid grid) {
        grid.pickRandomCaps().ifPresent(grid::contamine);
    }

    private static void hitRandomCaps(Grid grid) {
        grid.pickRandomCaps().ifPresent(grid::hit);
    }
}
