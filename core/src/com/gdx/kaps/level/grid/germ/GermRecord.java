package com.gdx.kaps.level.grid.germ;

import java.util.Objects;

public enum GermRecord {
    BASIC("basic", 1, 0, 8),
    WALL("wall", 4, 0, 4),
    VIRUS("virus", 1, 7, 8),
    THORN("thorn", 1, 5, 8),
    ;

    private final String name;
    private final int cooldown;
    private final int nbFrames;
    private final int maxHP;

    GermRecord(String name, int maxHP, int cooldown, int frames) {
        Objects.requireNonNull(name);
        if (maxHP < 0) throw new IllegalArgumentException("Germ can't have a negative health (" + maxHP + ")");
        if (cooldown < 0) throw new IllegalArgumentException("Germ can't have a negative cooldown (" + cooldown + ")");

        this.cooldown = cooldown;
        this.nbFrames = frames;
        this.maxHP = maxHP;
        this.name = name;
    }

    public String type() {
        return name;
    }

    public int maxHP() {
        return maxHP;
    }

    public int nbFrames() {
        return nbFrames;
    }
}
