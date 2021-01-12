package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.ShapeRendererAdaptor;
import com.gdx.kaps.level.caps.Caps;
import com.gdx.kaps.level.caps.Gelule;
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
    static class Column implements Iterable<Optional<Caps>> {
        private final Caps[] tiles;

        Column(int size) {
            tiles = new Caps[size];
        }

        @Override
        public Iterator<Optional<Caps>> iterator() {
            return new Iterator<>() {
                private int index;

                @Override
                public boolean hasNext() {
                    return index < tiles.length;
                }

                @Override
                public Optional<Caps> next() {
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

    private Optional<Caps> get(Position position) {
        return get(position.x(), position.y());
    }

    private Optional<Caps> get(int x, int y) {
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
    private void set(int x, int y, Caps caps) {
        columns[x].tiles[y] = caps;
    }

    private void pop(Caps caps) {
        get(caps.linkedPosition()).ifPresent(Caps::unlink);
        pop(caps.x(), caps.y());
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
            // TODO: drop all caps and save a boolean
            //  telling if more caps can drop.
            // IMPL: find a cleaner way to do it
            for (var col : this) {
                for (var opt : col) {
                    opt.ifPresent(caps -> {
                        if (caps.isLinked()) {
                            // TODO : drop gelule -> drop(caps, linked)
                            // IMPL: "linked" field in Caps that indicates Caps linked to.
                            //  gelule -> supp, operations on this && this.linked
                        } else {
                            canDrop[0] |= dip(caps);
                        }
                    });
                }
            }
        }
    }

    // TODO: doc that explicits importance of dipping caps + change position in array

    /**
     * Dips the provided caps and updates its position into the grid if needed
     * @param caps the caps to dip
     * @return true if the caps was dipped, false if its positon is unchanged
     */
    private boolean dip(Caps caps) {
        if (caps.dip(this)) {
            set(caps.x(), caps.y(), caps);
            pop(caps.x(), caps.y() + 1);
            return true;
        }
        return false;
    }


    boolean deleteMatches() {
        var toDelete = new HashSet<Caps>();
        forEach(c -> c.forEach(opt -> opt.ifPresent(caps -> {
            var leftCaps = IntStream.range(0, MIN_MATCH_RANGE)
              .mapToObj(n -> get(caps.x() - n, caps.y()))
              .collect(Collectors.toList());

            var bottomCaps = IntStream.range(0, MIN_MATCH_RANGE)
              .mapToObj(n -> get(caps.x(), caps.y() - n))
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
    private List<Caps> matchingCapsFrom(List<Optional<Caps>> match) {
        var color = requireNonNull(match).stream()
                      .map(opt -> opt.map(Caps::color).orElse(null))
                      .filter(Objects::nonNull)
                      .collect(Collectors.toList());

        if (color.size() >= MIN_MATCH_RANGE && color.stream().allMatch(color.get(0)::equals)) {
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
        forEach(c -> c.forEach(opt -> opt.ifPresent(Caps::render)));
    }
}
