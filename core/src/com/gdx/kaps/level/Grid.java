package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.ShapeRendererAdaptor;
import com.gdx.kaps.level.caps.Caps;
import com.gdx.kaps.level.caps.Gelule;
import com.gdx.kaps.level.caps.GridObject;
import com.gdx.kaps.level.caps.Position;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public class Grid implements Iterable<Grid.Column>, Renderable {
    private final static int MIN_MATCH_RANGE = 4;
    // IMPL: put in level ? since it's a level rule
    // TODO: find a way better name
    static class Column implements Iterable<Optional<GridObject>> {
        // IMPL: stocks grid objects instead of caps.
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
     * @param obj the object from which we need the linked object
     * @return the grid object to which is linked {@code obj}.
     */
    public GridObject getLinked(GridObject obj) {
        // TODO: always given 'linked pos'. new method name ?
        requireNonNull(obj);
        if (!obj.isLinked()) {
            throw new IllegalStateException(obj + " is not even linked.");
        }
        return get(obj.x(), obj.y()).orElse(obj);
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
    private void set(int x, int y, GridObject caps) {
        columns[x].tiles[y] = caps;
    }

    private void pop(GridObject obj) {
        getLinked(obj).unlink();
        pop(obj.x(), obj.y());
    }
    private void pop(int x, int y) {
        set(x, y, null);
    }

    private void accept(Caps caps) {
        requireNonNull(caps);
        set(caps.x(), caps.y(), (caps));
    }

    void accept(Gelule gelule) {
        requireNonNull(gelule);
        gelule.forEach(this::accept);
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

    // TODO: doc that explicits importance of dipping caps + change position in array
    /**
     * Dips the provided obj and updates its position into the grid if needed
     * @param obj the grid object to dip
     * @return true if the obj was dipped, false if its positon is unchanged
     */
    private boolean dip(GridObject obj) {
        requireNonNull(obj);
        if (obj.isLinked()) {
            if (obj.canDip(this) && requireNonNull(get(obj.linkedPosition()).orElse(null)).canDip(this)) {
                obj.dip(this);
                get(obj.linkedPosition()).ifPresent(o -> o.dip(this));
                return true;
            }
            return false;
        }
        else if (obj.dip(this)) {
            set(obj.x(), obj.y(), obj);
            pop(obj.x(), obj.y() + 1);
            return true;
        }
        return false;
    }

    boolean deleteMatches() {
        var toDelete = new HashSet<GridObject>();
        forEach(c -> c.forEach(opt -> opt.ifPresent(obj -> {
            var leftCaps = IntStream.range(0, MIN_MATCH_RANGE)
              .mapToObj(n -> get(obj.x() - n, obj.y()))
              .collect(Collectors.toList());

            var bottomCaps = IntStream.range(0, MIN_MATCH_RANGE)
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

        if (colors.size() >= MIN_MATCH_RANGE && colors.stream().allMatch(colors.get(0)::equals)) {
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
        forEach(c -> c.forEach(opt -> opt.ifPresent(GridObject::render)));
    }
}
