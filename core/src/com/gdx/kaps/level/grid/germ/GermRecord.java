package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.grid.GridObject;

import java.util.Objects;

public enum GermRecord { // IMPL: use a record if possible
    BASIC("basic", 'B', 1, 0),
    WALL("wall", 'W', 4, 0),
    VIRUS("virus", 'V', 1, 7),
    THORN("thorn", 'T', 1, 5),
    ;

    private final String name;
    private final char symbol;
    private final int cooldown;
    private final int maxHP;

    GermRecord(String name, char symbol, int maxHP, int cooldown) {
        Objects.requireNonNull(name);
        if (maxHP < 0) throw new IllegalArgumentException("Germ can't have a negative health (" + maxHP + ")");
        if (cooldown < 0) throw new IllegalArgumentException("Germ can't have a negative cooldown (" + cooldown + ")");

        this.name = name;
        this.symbol = symbol;
        this.cooldown = cooldown;
        this.maxHP = maxHP;
    }

    public String type() {
        return name;
    }

    public int maxHP() {
        return maxHP;
    }

    public static Germ parseSymbol(char symbol) {
        for (var type : values()) {
            if (type.symbol == symbol) {

            }
        }
        return null;
    }
}
