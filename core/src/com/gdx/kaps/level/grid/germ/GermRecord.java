package com.gdx.kaps.level.grid.germ;

import java.util.Objects;

public enum GermRecord { // IMPL: use a record if possible
    BASIC("basic", 'B', 1, 0, 8),
    WALL("wall", 'W', 4, 0, 4),
    VIRUS("virus", 'V', 1, 7, 8),
    THORN("thorn", 'T', 1, 5, 8),
    ;

    private final String name;
    private final char symbol;
    private final int cooldown;
    private final int nbFrames;
    private final int maxHP;

    GermRecord(String name, char symbol, int maxHP, int cooldown, int frames) {
        Objects.requireNonNull(name);
        if (maxHP < 0) throw new IllegalArgumentException("Germ can't have a negative health (" + maxHP + ")");
        if (cooldown < 0) throw new IllegalArgumentException("Germ can't have a negative cooldown (" + cooldown + ")");

        this.cooldown = cooldown;
        this.symbol = symbol;
        this.maxHP = maxHP;
        this.name = name;
        this.nbFrames = frames;
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
