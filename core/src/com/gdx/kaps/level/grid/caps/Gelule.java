package com.gdx.kaps.level.grid.caps;

import com.badlogic.gdx.math.Rectangle;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import static java.util.Objects.requireNonNull;

public class Gelule implements Iterable<LinkedCaps> {
    private final LinkedCaps linked;
    private final LinkedCaps main;

    public Gelule(Level lvl) {
        // IMPL: remove level from arguments and set it position directly at spawning
        this(lvl, lvl.randomColor(), lvl.randomColor());
    }

    private Gelule(Level lvl, Color color) {
        this(lvl, color, color);
    }

    private Gelule(Level lvl, Color c1, Color c2) {
        requireNonNull(lvl);
        int x = lvl.gridWidth() /2 -1;
        int y = lvl.gridHeight() -1;
        main = new LinkedCaps(x, y, Look.LEFT, c1);
        linked = new LinkedCaps(x+1, y, Look.RIGHT, c2);
        linked.linkTo(main);
    }

    private Gelule(Level lvl, Caps.Type type) {
        requireNonNull(lvl);
        boolean left = new Random().nextBoolean();
        int x = lvl.gridWidth() /2 -1;
        int y = lvl.gridHeight() -1;
        main = new LinkedCaps(x, y, Look.LEFT, lvl.randomColor(), left ? Caps.Type.BASIC : type);
        linked = new LinkedCaps(x+1, y, Look.RIGHT, lvl.randomColor(), left ? type : Caps.Type.BASIC);
        linked.linkTo(main);
    }

    Gelule(Gelule gelule) {
        requireNonNull(gelule);
        main = new LinkedCaps(gelule.main);
        linked = new LinkedCaps(gelule.linked);
        linked.linkTo(main);
    }

    private Gelule(Gelule gelule, Look look) {
        requireNonNull(gelule);
        requireNonNull(look);
        main = new LinkedCaps(gelule.main).shifted(look);
        linked = new LinkedCaps(gelule.linked).shifted(look);
        linked.linkTo(main);
    }

    Gelule(Gelule color, Gelule pos) {
        requireNonNull(color);
        requireNonNull(pos);
        main = new LinkedCaps(pos.x(), pos.y(), pos.main.look(), color.main.color());
        linked = new LinkedCaps(pos.linked.x(), pos.linked.y(), pos.linked.look(), color.linked.color());
        linked.linkTo(this.main);
    }

    public static Gelule singleColored(Level lvl, Color color) {
        return new Gelule(lvl, color);
    }

    public static Gelule withPower(Level lvl, Caps.Type type) {
        return new Gelule(lvl, type);
    }

    // getters

    public int x() {
        return main.x();
    }

    public int y() {
        return main.y();
    }

    public Gelule copy() {
        return new Gelule(this);
    }

    public static Gelule copyColorOf(Gelule color, Gelule pos) {
        return new Gelule(color, pos);
    }

    /**
     * @param look the direction in which the thing must be shifted
     * @return an identical object, but shifted by look's (x, y) vector
     */
    private Gelule shifted(Look look) {
        return new Gelule(this, look);
    }

    // predicates

    public boolean isAtValidEmplacement(Grid grid) {
        return isInGrid(grid) && !collidesPile(grid);
    }

    public boolean isInGrid(Grid grid) {
        return main.isInGrid(grid) && linked.isInGrid(grid);
    }

    public boolean collidesPile(Grid grid) {
        return main.collidesPile(grid) || linked.collidesPile(grid);
    }

    /**
     * tries flipping the gelule and evaluates its position.
     * @return true if the flipped gelule stands in a valid position, false if not.
     */
    private boolean canFlip(Grid grid) {
        var copy = copy();
        copy.flip();

        if (!copy.linked.isAtValidEmplacement(grid)) {
            return copy.shifted(copy.main.look()).isAtValidEmplacement(grid);
        }
        return true;
    }

    private boolean canMove(Look look, Grid grid) {
        return shifted(look).isAtValidEmplacement(grid);
    }

    // movement

    private void move(Look look) {
        main.move(look);
        updateLinked();
    }

    private void flip() {
        main.flip();
        updateLinked();
    }

    private boolean moveIfPossible(Look look, Grid grid) {
        if (!canMove(look, grid)) return false;
        move(look);
        return true;
    }

    public boolean moveLeftIfPossible(Grid grid) {
        return moveIfPossible(Look.LEFT, grid);
    }

    public boolean moveRightIfPossible(Grid grid) {
        return moveIfPossible(Look.RIGHT, grid);
    }

    public boolean dipIfPossible(Grid grid) {
        return moveIfPossible(Look.DOWN, grid);
    }

    public boolean flipIfPossible(Grid grid) {
        if (!canFlip(grid)) return false;
        flip();

        // TODO: a 'helpFlip' method that replaces flipped thing
        if (!linked.isAtValidEmplacement(grid)) {
            move(main.look());
        }
        return true;
    }

    // update

    private void updateLinked() {
        main.updateLinked();
    }

    @Override
    public Iterator<LinkedCaps> iterator() {
        return new Iterator<>() {
            private final LinkedCaps[] both = new LinkedCaps[]{main, linked};
            private int index;

            public boolean hasNext() {
                return index < both.length;
            }

            public LinkedCaps next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return both[index++];
            }
        };
    }

    @Override
    public String toString() {
        return "(" + main + "|" + linked + ")";
    }

    public void render() {
        main.render();
        linked.render();
    }

    public void render(Rectangle zone) {
        main.render(zone.x, zone.y, zone.width/2, zone.height);
        linked.render(zone.x + zone.width/2, zone.y, zone.width/2, zone.height);
    }
}
