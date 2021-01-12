package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.ShapeRendererAdaptor;
import com.gdx.kaps.level.caps.Gelule;
import com.gdx.kaps.level.caps.GridObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public class Grid implements Iterable<Grid.Column>, Renderable {
    static class Column implements Iterable<Optional<GridObject>> {
        private final GridObject[] tiles;

        Column(int size) {
            tiles = new GridObject[size];
        }

        @Override
        public Iterator<Optional<GridObject>> iterator() {
            return new Iterator<>() {
                private int index;

                @Override
                public boolean hasNext() {
                    return index < tiles.length;
                }

                @Override
                public Optional<GridObject> next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Optional.ofNullable(tiles[index++]);
                }
            };
        }
    }
    private final ShapeRendererAdaptor sra = new ShapeRendererAdaptor();
    // IMPL: put in level ? since it's a level rule
    // TODO: find a way better name
    private final Column[] columns;

    Grid(int width, int height) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException("Invalid grid size: " + width + "x" + height);
        }

        columns = new Column[width];

        for (int i = 0; i < width; i++) {
            columns[i] = new Column(height);
        }
    }

    // getters
    public int width() {
        return columns.length;
    }

    public int height() {
        return columns[0].tiles.length;
    }

    /**
     * @param obj the object from which we need the linked object. Must be linked.
     * @return the grid object to which is linked {@code obj}.
     * @throws IllegalStateException if {@code obj} is not linked.
     */
    public GridObject getLinked(GridObject obj) {
        requireNonNull(obj);
        if (!obj.isLinked()) {
            throw new IllegalStateException(obj + " is not even linked.");
        }
        return get(obj.linkedX(), obj.linkedY()).orElse(obj);
    }

    private Optional<GridObject> get(int x, int y) {
        if (!isInGrid(x, y)) return Optional.empty();
        return Optional.ofNullable(columns[x].tiles[y]);
    }

    // predicates
    public boolean isInGrid(int x, int y) {
        return 0 <= x && x < width() &&
                 0 <= y && y < height();
    }

    public boolean collidesPile(int x, int y) {
        return get(x, y).isPresent();
    }

    // operations
    public void set(GridObject obj) {
        set(obj.x(), obj.y(), obj);
    }

    private void set(int x, int y, GridObject caps) {
        columns[x].tiles[y] = caps;
    }

    private void pop(GridObject obj) {
        if (obj.isLinked()) obj.linked().unlink();
        pop(obj.x(), obj.y());
    }

    private void pop(int x, int y) {
        set(x, y, null);
    }

    void accept(Gelule gelule) {
        requireNonNull(gelule);
        var linked = gelule.linked();
        set(gelule);
        set(linked);
    }

    public void dropAll() {
        // ugly, but required to be used in lambdas.
        final boolean[] canDrop = {true};

        while (canDrop[0]) {
            canDrop[0] = false;
            forEach(
              col -> col.forEach(
                opt -> opt.ifPresent(obj -> canDrop[0] |= dip(obj))
              )
            );
        }
    }

    /**
     * Dips the provided obj and updates its position into the grid if needed
     * @param obj the grid object to dip
     * @return true if the obj was dipped, false if its positon is unchanged
     */
    // IMPL: when a grid object is moved, its position should be
    //  updated depending of its grid position
    private boolean dip(GridObject obj) {
        // FIXME: vertical gelules don't dip
        requireNonNull(obj);
        if (obj.dip()) {
            set(obj);
            pop(obj.x(), obj.y() + 1);
            return true;
        }
        return false;
    }

    /**
     * Deletes all matches of {@code MIN_MATCH_RANGE} grid objects of same color in a row.
     * @return true if matches were deleted, false if not.
     */
    boolean deleteMatches() {
        var toDelete = new HashSet<GridObject>();
        forEach(c -> c.forEach(opt -> opt.ifPresent(obj -> {
            var leftCaps = IntStream.range(0, Level.MIN_MATCH_RANGE)
              .mapToObj(n -> get(obj.x() - n, obj.y()))
              .collect(Collectors.toList());

            var bottomCaps = IntStream.range(0, Level.MIN_MATCH_RANGE)
              .mapToObj(n -> get(obj.x(), obj.y() - n))
              .collect(Collectors.toList());

            toDelete.addAll(matchingCapsFrom(leftCaps));
            toDelete.addAll(matchingCapsFrom(bottomCaps));
        })));
        toDelete.forEach(this::pop);
        return !toDelete.isEmpty();
    }

    /**
     * Takes a list of {@link Optional} caps, checks if they are all there and of the same color.
     * If yes, adds them to {@code toDelete}.
     * @param match the list of caps to check
     * @return a {@link Collection} of the caps to delete.
     * The collection is empty if the caps don't match.
     */
    private List<GridObject> matchingCapsFrom(List<Optional<GridObject>> match) {
        // IMPL: reduce list into a boolean telling if colors are 4 and of the same color
        var colors = requireNonNull(match).stream()
                      .map(opt -> opt.map(GridObject::color).orElse(null))
                      .filter(Objects::nonNull)
                      .collect(Collectors.toList());

        if (colors.size() >= Level.MIN_MATCH_RANGE && colors.stream().allMatch(colors.get(0)::equals)) {
            return match.stream()
              .map(Optional::get)
              .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public Iterator<Column> iterator() {
        return new Iterator<>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < columns.length;
            }

            @Override
            public Column next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (columns[index++]);
            }
        };
    }

    @Override
    public void render() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                float shift = (x + y) %2 == 0 ? 0.025f : 0;
                Color color = new Color(0.45f + shift, 0.5f + shift, 0.6f + shift, 1);

                sra.drawRect(
                  dim.boardMargin + x * dim.tile.width,
                  dim.boardMargin + y * dim.tile.height,
                  dim.tile.width,
                  dim.tile.height,
                  color
                );
            }
        }
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                int finalX = x;
                int finalY = y;
                get(x, y).ifPresent(obj -> obj.render(finalX, finalY));
            }
        }
    }
}
