package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.SoundStream;
import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.renderer.AnimatedSprite;

import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public abstract class Germ extends GridObject {
    private final AnimatedSprite sprite;
    private final boolean hasHealth;
    private final GermRecord type;
    private final Gauge health;
    private final int mana;

    Germ(int x, int y, Color color, GermRecord type) {
        this(x, y, color, type, type.maxHP());
    }

    Germ(int x, int y, Color color, GermRecord type, int health) {
        super(x, y, color);
        requireNonNull(type);
        if (0 >= health || health > type.maxHP())
            throw new IllegalArgumentException("Invalid health given to " + type.name() + " (" + health + ")" );

        this.type = type;
        hasHealth = type.maxHP() > 1;
        this.health = new Gauge(health, type.maxHP());
        mana = hasHealth ? health : type.mana();
        sprite = new AnimatedSprite(
          "android/assets/img/" + color().id() +
            "/germs/" + typeName() + "/idle_",
          type.nbFrames(),
          100_000_000
        );
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

    @Override
    public int points() {
        return isDestroyed() ? 50 : 10;
    }

    @Override
    public int mana() {
        return mana;
    }

    public String typeName() {
        return type.toString() + (hasHealth ? Math.max(health.value(), 1) : "");
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
        return health.isEmpty();
    }

    public boolean hasCooldown() {
        return false;
    }

    @Override
    public void hit() {
        health.decrease();
        if (!isDestroyed()) {
            sprite.updatePath(
              "android/assets/img/" + color().id() +
                "/germs/" + typeName() + "/idle_"
            );
        }
    }

    @Override
    public void dipIfPossible(Grid grid) {
    }

    void trigger(Level lvl) {
        playSound(type.sound());
        type.power().accept(lvl, this);
    }

    public void triggerIfReady(Level lvl) {
    }

    public void decreaseCooldown() {
    }

        // update
    @Override
    public void update() {
        sprite.update();
    }

    @Override
    public String toString() {
        return "{" + x() + ',' + y() + '}';
    }

    public void render(float x, float y, float width, float height) {
        sprite.render(x, y, width, height);
        if (hasHealth) {
            health.render(x, y, width, height/10,
              new com.badlogic.gdx.graphics.Color(1, 1, 1, 0.25f),
              com.badlogic.gdx.graphics.Color.WHITE
            );
        }
    }
}

