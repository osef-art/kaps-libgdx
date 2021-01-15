package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.ShapeRendererAdaptor;
import com.gdx.kaps.level.caps.Gelule;
import com.gdx.kaps.level.caps.Caps;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public class Grid implements Renderable {
    static class Column {
        private final Caps[] tiles;

        Column(int size) {
            tiles = new Caps[size];
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

    public Optional<Caps> get(int x, int y) {
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

    // transactions

    public void set(Caps obj) {
        set(obj.x(), obj.y(), obj);
    }

    private void set(int x, int y, Caps caps) {
        columns[x].tiles[y] = caps;
    }
    /**
     * Sets the grid element at coordinates (x, y) to null, no matter what it is.
     * @param x the row number
     * @param y the column number
     */
    private void remove(int x, int y) {
        set(x, y, null);
    }

    /**
     * Removes a whole object from the grid, meaning both parts of it if there are two.
     * @param obj the object to remove from grid
     */
    private void remove(Caps obj) {
        remove(obj.x(), obj.y());
        obj.linked().ifPresent(linked -> remove(linked.x(), linked.y()));
    }

    /**
     * Pops a grid object depending on its inner indexes.
     * @param obj the grid object to pop.
     */
    private void pop(Caps obj) {
        obj.linked().ifPresent(this::unlink);
        remove(obj.x(), obj.y());
    }

    /**
     * Sets grid element located at (x, y) to an unlinked Caps.
     * @param obj the element to unlink
     */
    private void unlink(Caps obj) {
        set(obj.x(), obj.y(), obj.unlinked());
    }


    /**
     * Dips the provided obj and updates its position into the grid if needed
     * @param obj the grid object to dip
     * @return true if the obj was dipped, false if its positon is unchanged
     */
    // IMPL: when a grid object is moved, its position should be
    //  updated depending of its grid position
    private boolean dipIfPossible(Caps obj) {
        // FIXME: gros bordel >.<
        // IMPL: chosen strategy:
        //  remove object from grid (but not deleting it)
        //  make it dip depending on the grid pile
        //  re-put it in the grid (success or not)
        requireNonNull(obj);

        remove(obj);
        var couldDip = obj.dipIfPossible();
        set(obj);
        return couldDip;
    }

    void accept(Gelule gelule) {
        requireNonNull(gelule);
        gelule.forEach(this::set);
    }

    // full grid operations

    public void dropAll() {
        // ugly, but required to be used in lambdas.
        final boolean[] canDrop = {true};

        while (canDrop[0]) {
            canDrop[0] = false;
            everyCapsInGrid().forEach(
               obj -> {} /*canDrop[0] |= dipIfPossible(obj)*/
            );
        }
    }

    /**
     * Deletes all matches of {@code MIN_MATCH_RANGE} grid objects of same color in a row.
     * @return true if matches were deleted, false if not.
     */
    boolean deleteMatches() {
        var toDelete = new HashSet<Caps>();
        everyCapsInGrid().forEach(obj -> {
            var leftCaps = IntStream.range(0, Level.MIN_MATCH_RANGE)
                             .mapToObj(n -> get(obj.x() - n, obj.y()))
                             .collect(Collectors.toList());

            var bottomCaps = IntStream.range(0, Level.MIN_MATCH_RANGE)
                               .mapToObj(n -> get(obj.x(), obj.y() - n))
                               .collect(Collectors.toList());

            toDelete.addAll(matchingCapsFrom(leftCaps));
            toDelete.addAll(matchingCapsFrom(bottomCaps));
        });
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
        // IMPL: reduce list into a boolean telling if colors are 4 and of the same color
        var colors = requireNonNull(match).stream()
                      .map(opt -> opt.map(Caps::color).orElse(null))
                      .filter(Objects::nonNull)
                      .collect(Collectors.toList());

        if (colors.size() >= Level.MIN_MATCH_RANGE && colors.stream().allMatch(colors.get(0)::equals)) {
            return match.stream()
              .map(Optional::get)
              .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private Stream<Caps> everyCapsInGrid() {
        return Arrays.stream(columns)
          .flatMap(col -> Arrays.stream(col.tiles))
          .filter(Objects::nonNull);
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
