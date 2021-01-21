package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.Zone;

import java.util.Optional;
import java.util.Set;

import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public abstract class Germ extends GridObject {
    private final AnimatedSprite sprite;
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

        sprite = new AnimatedSprite(
          "android/assets/img/" + color().id() +
            "/germs/" + type.type() +
            (type.maxHP() == 1 ? "" : health) + "/idle_",
          type.nbFrames(),
          100_000_000
        );
        this.health = health;
        this.type = type;
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
        if (!isDestroyed()) {
            sprite.updatePath("android/assets/img/" + color().id() + "/germs/" + type.type() + (type.maxHP() == 1 ? "" : health) + "/idle_");
            sprite.updateSprite();
        }
    }

    @Override
    public void dipIfPossible(Grid grid) {
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

    @Override
    public void render(int x, int y) {
        render(
          dim.gridMargin + x * dim.get(Zone.TILE).height,
          dim.topTile(y),
          dim.get(Zone.TILE).width,
          dim.get(Zone.TILE).height
        );
    }

    public void render(float x, float y, float width, float height) {
        sprite.render(x, y, width, height);
    }
}

