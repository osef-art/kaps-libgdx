package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;

import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public abstract class Germ extends GridObject {
    private final GermRecord type;
    private int health;

    Germ(int x, int y, Color color, GermRecord type) {
        this(x, y, color, type, type.maxHP());
    }

    Germ(int x, int y, Color color, GermRecord type, int health) {
        super(x, y, color);
        requireNonNull(type);
        if (health > type.maxHP())
            throw new IllegalArgumentException("Too much health given to " + type.name() + " (" + health + ")" );

        this.health = health;
        this.type = type;
        updateSprite();
    }

    public static Germ of(int x, int y, char symbol, Set<Color> colors) {
        switch (symbol) {
            case 'B':
                return new BasicGerm(x, y, Color.random(colors));
            case 'W':
                return new WallGerm(x, y, Color.random(colors), 2);
            case 'X':
                return new WallGerm(x, y, Color.random(colors), 3);
            case 'Y':
                return new WallGerm(x, y, Color.random(colors));
            case 'T':
                return new ThornGerm(x, y, Color.random(colors));
            case 'V':
                return new VirusGerm(x, y, Color.random(colors));
            case '.':
                return null;
            default:
                throw new IllegalStateException("Couldn't resolve symbol: " + symbol);
        }
    }

    // getters

    @Override
    public Optional<GridObject> linked() {
        return Optional.empty();
    }

    @Override
    public GridObject unlinked() {
        return this;
    }

    // predicates

    @Override
    public boolean canDip(Grid grid) {
        return false;
    }

    @Override
    public boolean isGerm() {
        return true;
    }

    @Override
    public boolean isDestroyed() {
        return health <= 0;
    }

    @Override
    public void hit() {
        health--;
        if (!isDestroyed()) updateSprite(x(), y(), "android/assets/img/" + color().id() + "/germs/" + type.type() + (type.maxHP() == 1 ? "" : health) + "/idle_0.png");

    }

    @Override
    public void dipIfPossible(Grid grid) {
    }

    // update

    void updateSprite() {
        updateSprite(x(), y(),
          "android/assets/img/" + color().id() +
            "/germs/" + type.type() +
            (type.maxHP() == 1 ? "" : health) +
            "/idle_0.png"
        );
    }

    @Override
    public String toString() {
        return "{" + x() + ',' + y() + '}';
    }

}

