package com.gdx.kaps.level.caps;

import com.gdx.kaps.level.Grid;
import com.gdx.kaps.level.Level;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Gelule implements Iterable<LinkedCaps> {
    private final LinkedCaps main;
    private final LinkedCaps linked;
    private final Grid grid;
    // IMPL: replace all 'linked' by main.linked ?

    public Gelule(Level lvl) {
        Objects.requireNonNull(lvl);
        grid = lvl.grid();
        int x = grid.width() /2 -1;
        int y = grid.height() -1;
        main = new LinkedCaps(x, y, Look.LEFT, lvl);
        linked = new LinkedCaps(x+1, y, Look.RIGHT, lvl);
        linked.linkTo(main);
    }

    private Gelule(Gelule gelule, Look look) {
        Objects.requireNonNull(gelule);
        Objects.requireNonNull(look);
        grid = gelule.grid;
        main = new LinkedCaps(gelule.main).shifted(look);
        linked = new LinkedCaps(gelule.linked).shifted(look);
    }

    // getters

    public int x() {
        return main.x();
    }

    public int y() {
        return main.y();
    }

    private Gelule copy() {
        return new Gelule(this, Look.NONE);
    }

    /**
     * @param look the direction in which the thing must be shifted
     * @return an identical object, but shifted by look's (x, y) vector
     */
    private Gelule shifted(Look look) {
        return new Gelule(this, look);
    }

    // predicates

    public boolean isAtValidEmplacement() {
        return isInGrid() && !collidesPile();
    }

    public boolean isInGrid() {
        return main.isInGrid() && linked.isInGrid();
    }

    public boolean collidesPile() {
        return main.collidesPile() || linked.collidesPile();
    }

    /**
     * tries flipping the gelule and evaluates its position.
     * @return true if the flipped gelule stands in a valid position, false if not.
     */
    private boolean canFlip() {
        var copy = copy();
        copy.flip();

        if (!copy.linked.isAtValidEmplacement()) {
            return copy.shifted(copy.main.look()).isAtValidEmplacement();
        }
        return true;
    }

    private boolean canMove(Look look) {
        return shifted(look).isAtValidEmplacement();
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

    private boolean moveIfPossible(Look look) {
        if (!canMove(look)) return false;
        move(look);
        return true;
    }

    public void moveLeftIfPossible() {
        moveIfPossible(Look.LEFT);
    }

    public void moveRightIfPossible() {
        moveIfPossible(Look.RIGHT);
    }

    public boolean dipIfPossible() {
        return moveIfPossible(Look.DOWN);
    }

    public void flipIfPossible() {
        if (!canFlip()) return;
        // TODO: prevent flip if a caps bothers
        flip();

        // TODO: a 'helpFlip' method that replaces flipped thing
        if (!linked.isAtValidEmplacement()) {
            move(main.look());
        }
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
}
