package com.gdx.kaps.level.grid.germ;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.Position;

import java.util.Optional;
import java.util.Set;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public abstract class Germ implements GridObject {
    // TODO: abstract class for shared fiels ?
    private final GermRecord type;
    private final Sprite sprite;
    private final Color color;
    final Position position;
    private int health;

    Germ(int x, int y, Color color, GermRecord type) {
        requireNonNull(color);
        requireNonNull(type);

        sprite = new Sprite(new Texture(
          "img/" + color.id() +
            "/germs/" + type.type() + "/idle_0.png"
        ));
        sprite.flip(false, true);
        position = new Position(x, y);
        health = type.maxHP();
        this.color = color;
        this.type = type;
    }

    public static Germ of(int x, int y, char symbol, Set<Color> colors) {
        Germ germ;
        switch (symbol) {
            case 'B':
                germ = new BasicGerm(x, y, Color.random(colors));
                break;
            case '.':
                germ = null;
                break;
            default:
                throw new IllegalStateException("Couldn't resolve symbol: " + symbol);
        }
        return germ;
    }

    @Override
    public int x() {
        return position.x();
    }

    @Override
    public int y() {
        return position.x();
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

    @Override
    public boolean canDip() {
        return false;
    }

    @Override
    public void dipIfPossible() {
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

