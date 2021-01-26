package com.gdx.kaps.level.grid.germ;

import java.util.Objects;

public enum GermRecord {
    // TODO: handle germ powers
    BASIC("basic", 8, 1),
    WALL("wall", 4, 4),
    VIRUS("virus", 8, 1, 7),
    THORN("thorn", 8, 1, 5),
    ;

    private final String name;
    private final int cooldown;
    private final int nbFrames;
    private final int maxHP;

    GermRecord(String name, int frames, int maxHP) {
        this(name, frames, maxHP, 0);
    }

    GermRecord(String name, int frames, int maxHP, int cooldown) {
        Objects.requireNonNull(name);
        if (maxHP < 0) throw new IllegalArgumentException("Germ can't have a negative health (" + maxHP + ")");
        if (cooldown < 0) throw new IllegalArgumentException("Germ can't have a negative cooldown (" + cooldown + ")");

        this.cooldown = cooldown;
        this.nbFrames = frames;
        this.maxHP = maxHP;
        this.name = name;
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
}
