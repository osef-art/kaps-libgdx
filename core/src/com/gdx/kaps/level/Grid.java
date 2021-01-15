package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.ShapeRendererAdaptor;
import com.gdx.kaps.level.caps.Caps;
import com.gdx.kaps.level.caps.Gelule;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
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

    private boolean canDip(Caps caps) {
        boolean canDip = caps.canDip();
        canDip &= caps.linked().map(Caps::canDip).orElse(true);
        return canDip;
    }

    // transactions

    private void doOnCapsAndLinkedIfExists(Caps caps, Consumer<Caps> action) {
        action.accept(caps);
        caps.linked().ifPresent(action);
    }

    private void set(Caps caps) {
        doOnCapsAndLinkedIfExists(caps, c -> set(c.x(), c.y(), c));
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
     * Removes a whole caps from the grid, meaning both parts of it if there are two.
     * @param caps the caps to remove from grid
     */
    private void remove(Caps caps) {
        doOnCapsAndLinkedIfExists(caps, c -> remove(c.x(), c.y()));
    }

    /**
     * Pops a grid caps depending on its inner indexes.
     * @param caps the grid caps to pop.
     */
    private void pop(Caps caps) {
        get(caps.x(), caps.y()).flatMap(Caps::linked).ifPresent(this::unlink);
        remove(caps.x(), caps.y());
    }

    /**
     * Sets grid element located at (x, y) to an unlinked Caps.
     * @param caps the element to unlink
     */
    private void unlink(Caps caps) {
        set(caps.x(), caps.y(), caps.unlinked());
    }

    private void dip(Caps caps) {
        doOnCapsAndLinkedIfExists(caps, c -> {
            c.dipIfPossible();
            set(c);
        });
    }

    /**
     * Dips the provided caps and updates its position into the grid if needed
     * @param caps the grid caps to dip
     * @return true if the caps was dipped, false if its positon is unchanged
     */
    // IMPL: when a grid caps is moved, its position should be
    //  updated depending of its grid position
    private boolean dipIfPossible(Caps caps) {
        // FIXME: gros bordel >.<
        // IMPL: chosen strategy:
        //  remove caps from grid (but not deleting it)
        //  make it dip depending on the grid pile
        //  re-put it in the grid (success or not)
        requireNonNull(caps);

        remove(caps);
        if (!canDip(caps)) {
            set(caps);
            return false;
        }
        dip(caps);
        return true;
    }

    void accept(Gelule gelule) {
        requireNonNull(gelule);
        gelule.forEach(this::set);
    }

    // full grid operations

    void dropAll() {
        boolean canDrop;

        do {
            canDrop = everyCapsInGrid()
                        .map(obj -> false/*this::dipIfPossible*/)
                        .reduce((bool, bool2) -> bool || bool2)
                        .orElse(false);
        } while (canDrop);
    }

    /**
     * Deletes all matches of {@code MIN_MATCH_RANGE} grid caps of same color in a row.
     * @return true if matches were deleted, false if not.
     */
    boolean deleteMatches() {
        // TODO: huh ?? doesn't delete all matches :/
        var toDelete = new HashSet<Caps>();
        everyCapsInGrid().forEach(caps -> {
            if (caps.x() >= Level.MIN_MATCH_RANGE - 1) {
                toDelete.addAll(matchingCapsFrom(n -> get(caps.x() - n, caps.y())));
            }
            if (caps.y() >= Level.MIN_MATCH_RANGE - 1) {
                toDelete.addAll(matchingCapsFrom(n -> get(caps.x(), caps.y() - n)));
            }
        });
        toDelete.forEach(this::pop);
        return !toDelete.isEmpty();
    }

    /**
     * Takes a list of {@link Optional} caps, checks if they are all there and of the same color.
     * If yes, adds them to {@code toDelete}.
     * @return a {@link Collection} of the caps to delete.
     * The collection is empty if the caps don't match.
     */
    private List<Caps> matchingCapsFrom(IntFunction<Optional<Caps>> collector) {
        // TODO: clean this method. a lot
        // IMPL: reduce list into a boolean telling if colors are 4 and of the same color
        var colors = IntStream.range(0, Level.MIN_MATCH_RANGE)
                       .mapToObj(collector)
                      .map(opt -> opt.map(Caps::color).orElse(null))
                      .filter(Objects::nonNull)
                      .collect(Collectors.toList());

        if (colors.size() >= Level.MIN_MATCH_RANGE && colors.stream().allMatch(colors.get(0)::equals)) {
            return IntStream.range(0, Level.MIN_MATCH_RANGE)
                     .mapToObj(collector)
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
                get(x, y).ifPresent(caps -> caps.render(finalX, finalY));
            }
        }
    }
}
