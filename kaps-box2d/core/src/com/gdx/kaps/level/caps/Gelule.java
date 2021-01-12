package com.gdx.kaps.level.caps;

import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.Grid;

import java.util.*;

public class Gelule implements GridObject, Renderable, Iterable<Caps> {
    private final Caps main;
    private final Caps linked;
    // IMPL: give them the level so they know where they are ?
    //  communication w/ grid + more

    private Gelule(Gelule gelule, Look look) {
        Objects.requireNonNull(gelule);
        main = new Caps(gelule.main).shifted(look);
        linked = new Caps(gelule.linked).shifted(look);
    }

    public Gelule(Grid grid, Set<Color> colors) {
        int x = grid.width() /2 -1;
        int y = grid.height() -1;
        main = new Caps(x, y, Look.LEFT, Color.random(colors));
        linked = new Caps(x+1, y, Look.RIGHT, Color.random(colors));
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
     * @param grid the grid in which our gelule evolves
     * @return true if the flipped gelule stands in a valid position, false if not.
     */
    private boolean canFlip(Grid grid) {
        main.flip(grid);
        updateLinked();

        if (!linked.isAtValidEmplacement(grid)) {
            return shifted(main.look()).isAtValidEmplacement(grid);
        }
        return true;
    }

    // predicates
    @Override
    public boolean isAtValidEmplacement(Grid grid) {
        return isInGrid(grid) && !collidesPile(grid);
    }

    @Override
    public boolean isInGrid(Grid grid) {
        return main.isInGrid(grid) && linked.isInGrid(grid);
    }

    @Override
    public boolean collidesPile(Grid grid) {
        return main.collidesPile(grid) || linked.collidesPile(grid);
    }

    // movement
    private boolean move(Look look, Grid grid) {
        if (!shifted(look).isAtValidEmplacement(grid)) return false;
        main.move(look, grid);
        updateLinked();
        return true;
    }

    public void moveLeft(Grid grid) {
        move(Look.LEFT, grid);
    }

    public void moveRight(Grid grid) {
        move(Look.RIGHT, grid);
    }

    public boolean dip(Grid grid) {
        return move(Look.DOWN, grid);
    }

    public void flip(Grid grid) {
        if (!copy().canFlip(grid)) return;
        // INFO: prevent flip if a caps bothers
        main.flip(grid);
        updateLinked();

        // TODO: a 'helpFlip' method that replaces flipped thing
        if (!linked.isAtValidEmplacement(grid)) {
            move(main.look(), grid);
        }
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
}
