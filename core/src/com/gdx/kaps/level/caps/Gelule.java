package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.Grid;
import com.gdx.kaps.level.Level;

import java.util.*;

public class Gelule implements GridObject, Renderable, Iterable<Caps> {
    private final Grid grid;
    private final Caps main;
    private final Caps linked;
    // IMPL: give them the level so they know where they are ?
    //  communication w/ grid + more

    public Gelule(Level lvl) {
        Objects.requireNonNull(lvl);
        grid = lvl.grid();
        int x = grid.width() /2 -1;
        int y = grid.height() -1;
        main = new Caps(x, y, Look.LEFT, lvl);
        linked = new Caps(x+1, y, Look.RIGHT, lvl);
    }

    private Gelule(Gelule gelule, Look look) {
        Objects.requireNonNull(gelule);
        grid = gelule.grid;
        main = new Caps(gelule.main).shifted(look);
        linked = new Caps(gelule.linked).shifted(look);
    }

    // getters
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

    /**
     * tries flipping the gelule and evaluates its position.
     * @return true if the flipped gelule stands in a valid position, false if not.
     */
    private boolean canFlip() {
        main.flip();
        updateLinked();

        if (!linked.isAtValidEmplacement()) {
            return shifted(main.look()).isAtValidEmplacement();
        }
        return true;
    }

    // predicates
    @Override
    public boolean isAtValidEmplacement() {
        return isInGrid() && !collidesPile();
    }

    @Override
    public boolean isInGrid() {
        return main.isInGrid() && linked.isInGrid();
    }

    @Override
    public boolean collidesPile() {
        return main.collidesPile() || linked.collidesPile();
    }

    // movement
    private boolean move(Look look) {
        if (!shifted(look).isAtValidEmplacement()) return false;
        main.move(look);
        updateLinked();
        return true;
    }

    public void moveLeft() {
        move(Look.LEFT);
    }

    public void moveRight() {
        move(Look.RIGHT);
    }

    public boolean dip() {
        return move(Look.DOWN);
    }

    public void flip() {
        if (!copy().canFlip()) return;
        // TODO: prevent flip if a caps bothers
        main.flip();
        updateLinked();

        // TODO: a 'helpFlip' method that replaces flipped thing
        if (!linked.isAtValidEmplacement()) {
            move(main.look());
        }
    }

    @Override
    public void unlink() {
        main.unlink();
        linked.unlink();
        grid.set(main);
        grid.set(linked);
    }

    @Override
    public int linkedX() {
        return linked.x();
    }

    @Override
    public int linkedY() {
        return linked.y();
    }

    @Override
    public void render(int x, int y) {
        main.render(x, y);
    }

    @Override
    public int x() {
        return main.x();
    }

    @Override
    public int y() {
        return main.y();
    }

    @Override
    public Color color() {
        return main.color();
    }

    @Override
    public boolean isLinked() {
        return true;
    }

    private void updateLinked() {
        linked.linkTo(main);
    }

    @Override
    public Iterator<Caps> iterator() {
        return new Iterator<>() {
            private final Caps[] both = new Caps[]{main, linked};
            int index;

            @Override
            public boolean hasNext() {
                return index < both.length;
            }

            @Override
            public Caps next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return both[index++];
            }

            @Override
            public void remove() {
            }
        };
    }

    @Override
    public String toString() {
        return "(" + main + "|" + linked + ")";
    }

    @Override
    public void render() {
        main.render();
        linked.render();
    }

    @Override
    public GridObject linked() {
        return linked;
    }
}
