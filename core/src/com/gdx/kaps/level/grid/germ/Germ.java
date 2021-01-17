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
    // TODO: abstract class for shared fiels ?
    private final GermRecord type;
    private final Sprite sprite;
    private final Color color;
    final Position position;
    private int cooldown;
    private int health;
    private int maxHP;

    Germ(int x, int y, Color color, GermRecord type) {
        this(x, y, color, type, type.maxHP());
    }

    Germ(int x, int y, Color color, GermRecord type, int health) {
        requireNonNull(color);
        requireNonNull(type);

        sprite = new Sprite(new Texture(
          "android/assets/img/" + color.id() +
            "/germs/" + type.type() +
            (type.maxHP() == 1 ? "" : health + 1) +
            "/idle_0.png"
        ));
        sprite.flip(false, true);
        position = new Position(x, y);
        this.cooldown = type.cooldown();
        this.health = health;
        this.color = color;
        this.type = type;
    }

    public static Germ of(int x, int y, char symbol, Set<Color> colors) {
        Germ germ;
        switch (symbol) {
            case 'B':
                germ = new BasicGerm(x, y, Color.random(colors));
                break;
            case 'W':
                germ = new WallGerm(x, y, Color.random(colors), 1);
                break;
            case 'X':
                germ = new WallGerm(x, y, Color.random(colors), 2);
                break;
            case 'Y':
                germ = new WallGerm(x, y, Color.random(colors), 3);
                break;
            case 'Z':
                germ = new WallGerm(x, y, Color.random(colors));
                break;
            case 'T':
                germ = new ThornGerm(x, y, Color.random(colors));
                break;
            case 'V':
                germ = new VirusGerm(x, y, Color.random(colors));
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

    @Override
    public boolean canDip(Grid grid) {
        return false;
    }

    @Override
    public boolean isGerm() {
        return true;
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

