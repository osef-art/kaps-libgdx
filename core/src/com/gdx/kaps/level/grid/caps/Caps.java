package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;

import java.util.Optional;
import java.util.function.BiConsumer;

import static com.gdx.kaps.MainScreen.shake;
import static com.gdx.kaps.level.grid.caps.EffectAnim.EffectType.FIRE_FX;
import static java.util.Objects.requireNonNull;

public class Caps extends GridObject {
    public enum Type {
        BASIC("", (g, c) -> {}),
        BOMB("bomb_", Type::hitTilesAround),
        ;

        private final BiConsumer<Grid, Caps> power;
        private final String path;

        Type(String path, BiConsumer<Grid, Caps> power) {
            this.power = power;
            this.path = path;
        }

        public String path() {
            return path;
        }

        private static void hitTilesAround(Grid grid, Caps caps) {
            grid.hit(caps.x() - 1, caps.y() - 1, FIRE_FX);
            grid.hit(caps.x() - 1, caps.y(), FIRE_FX);
            grid.hit(caps.x() - 1, caps.y() + 1, FIRE_FX);
            grid.hit(caps.x(), caps.y() + 1, FIRE_FX);
            grid.hit(caps.x(), caps.y() - 1, FIRE_FX);
            grid.hit(caps.x() + 1, caps.y() - 1, FIRE_FX);
            grid.hit(caps.x() + 1, caps.y(), FIRE_FX);
            grid.hit(caps.x() + 1, caps.y() + 1, FIRE_FX);
            shake();
            caps.playSound("fire");
        }
    }
    private boolean destroyed;
    final Type type;

    Caps(LinkedCaps caps) {
        this(caps.x(), caps.y(), caps.color(), caps.type);
    }

    Caps(int x, int y, Color color, Type type) {
        super(x, y, color);
        requireNonNull(type);
        this.type = type;
        updateSprite();
    }

    Caps(Caps caps) {
        super(caps.x(), caps.y(), caps.color());
        requireNonNull(caps);
        destroyed = false;
        type = caps.type;
    }

    // getters

    @Override
    public Optional<GridObject> linked() {
        return Optional.empty();
    }

    private Caps copy() {
        return new Caps(this);
    }

    private Caps shifted(Look look) {
        requireNonNull(look);
        var copy = copy();
        copy.move(look);
        return copy;
    }

    @Override
    public Caps unlinked() {
        return this;
    }

    @Override
    public int points() {
        return 20;
    }

    @Override
    public int mana() {
        return 1;
    }

    // predicates

    @Override
    public boolean isGerm() {
        return false;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isInGrid(Grid grid) {
        requireNonNull(grid);
        return grid.isInGrid(x(), y());
    }

    public boolean collidesPile(Grid grid) {
        requireNonNull(grid);
        return grid.collidesPile(x(), y());
    }

    public boolean isAtValidEmplacement(Grid grid) {
        return isInGrid(grid) && !collidesPile(grid);
    }

    private boolean canMove(Look look, Grid grid) {
        return shifted(look).isAtValidEmplacement(grid);
    }

    // movement

    void move(Look look) {
        requireNonNull(look);
        pos().add(look.vector());
    }

    private void moveIfPossible(Look look, Grid grid) {
        if (!canMove(look, grid)) return;
        move(look);
    }

    @Override
    public boolean canDip(Grid grid) {
        return canMove(Look.DOWN, grid);
    }

    @Override
    public void dipIfPossible(Grid grid) {
        moveIfPossible(Look.DOWN, grid);
    }

    @Override
    public void hit() {
        destroyed = true;
    }

    @Override
    public void triggerOnDeath(Grid grid) {
        type.power.accept(grid, this);
    }

    @Override
    public void paint(Color color) {
        super.paint(color);
        updateSprite();
    }

// update

    private void updateSprite() {
        super.updateSprite(
          "android/assets/img/" + color().id() +
            "/caps/" + type.path + Look.NONE + ".png"
        );
    }

    @Override
    public void update() {
    }

    public String toString() {
        return "(" + x() + "," + y() + ")";
    }
}
