package com.gdx.kaps.level.grid.germ;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.Position;

import java.util.Optional;
import java.util.Set;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public abstract class Germ implements GridObject {
    // TODO: abstract class for fields shared w/ caps ?
    private final GermRecord type;
    private final Color color;
    final Position position;
    private Sprite sprite;
    private int health;

    Germ(int x, int y, Color color, GermRecord type) {
        this(x, y, color, type, type.maxHP());
    }

    Germ(int x, int y, Color color, GermRecord type, int health) {
        requireNonNull(color);
        requireNonNull(type);
        if (health > type.maxHP())
            throw new IllegalArgumentException("Too much health given to " + type.name() + " (" + health + ")" );

        position = new Position(x, y);
        this.health = health;
        this.color = color;
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
    public int x() {
        return position.x();
    }

    @Override
    public int y() {
        return position.y();
    }

    @Override
    public Color color() {
        return color;
    }

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
        if (!isDestroyed()) updateSprite();
    }

    private void updateSprite() {
        sprite = new Sprite(new Texture(
          "android/assets/img/" + color.id() +
            "/germs/" + type.type() +
            (type.maxHP() == 1 ? "" : health) +
            "/idle_0.png"
        ));
        sprite.flip(false, true);
    }

    @Override
    public void dipIfPossible(Grid grid) {
    }

    @Override
    public String toString() {
        return "{" + x() + ',' + y() + '}';
    }

    @Override
    public void render(int x, int y) {
        batch.begin();
        batch.draw(
          sprite,
          dim.gridMargin + x * dim.tile.height,
          dim.topTile(y),
          dim.tile.width,
          dim.tile.height
        );
        batch.end();
    }
}

